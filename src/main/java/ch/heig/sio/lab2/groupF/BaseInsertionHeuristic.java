package ch.heig.sio.lab2.groupF;

import ch.heig.sio.lab2.display.ObservableTspConstructiveHeuristic;
import ch.heig.sio.lab2.display.TspHeuristicObserver;
import ch.heig.sio.lab2.tsp.Edge;
import ch.heig.sio.lab2.tsp.TspData;
import ch.heig.sio.lab2.tsp.TspTour;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Gère l'insertion progressive des villes, tout en permettant aux sous-classes de définir leur stratégie d'insertion
 *
 * @author Jarod Streckeisen, Timothée Van Hove
 */
public abstract class BaseInsertionHeuristic implements ObservableTspConstructiveHeuristic {

    /**
     * Construit une tournée en insérant les villes progressivement.
     *
     * @param data           L'instance de données du TSP contenant les villes et les distances.
     * @param startCityIndex L'index de la ville de départ.
     * @param observer       Oservateur permettant de visualiser l'évolution du tour.
     * @return TspTour représentant la tournée finale et sa longueur totale.
     */
    @Override
    public TspTour computeTour(TspData data, int startCityIndex, TspHeuristicObserver observer) {

        List<Integer> tour = new ArrayList<>(); // Villes dans la tournée.
        int n = data.getNumberOfCities();
        tour.add(startCityIndex);

        // Initialise la liste des villes qui ne sont pas encore visitées.
        List<Integer> unvisitedCities = initializeUnvisitedCities(n, startCityIndex);

        // Initialise un tableau pour mémoriser la distance minimale entre chaque ville non visitée et la tournée actuelle.
        long[] minDistances = new long[n];
        for (int i = 0; i < n; i++) {
            if (i == startCityIndex) {
                minDistances[i] = Long.MAX_VALUE; // Distance infinie pour la ville de départ, déjà dans la tournée.
            } else {
                minDistances[i] = data.getDistance(startCityIndex, i); // Distance initiale par rapport à la ville de départ.
            }
        }

        long totalLength = 0;

        // Boucle jusqu'à ce que toutes les villes aient été insérées dans la tournée.
        while (!unvisitedCities.isEmpty()) {
            // Sélection de la prochaine ville à insérer (définie par la sous-classe) en fonction de la distance minimale mémorisée.
            int cityToInsert = selectCityToInsert(minDistances, unvisitedCities);

            // Trouver la meilleure position pour insérer la ville choisie dans la tournée.
            int bestPosition = findBestInsertionPosition(data, tour, cityToInsert);
            long minLengthIncrease = calculateLengthIncrease(data, tour, cityToInsert, bestPosition);
            
            // Insérer la ville dans la meilleure position et mettre à jour la longueur totale.
            tour.add(bestPosition, cityToInsert);
            totalLength += minLengthIncrease;

            // Mettre à jour les distances minimales des villes non visitées après l'insertion.
            updateMinDistances(data, minDistances, cityToInsert, unvisitedCities);

            // Notifier l'observateur pour visualiser la progression de la tournée.
            observer.update(tourToEdges(tour));
        }

        // Retourner la tournée finale avec sa longueur totale.
        return new TspTour(data, tour.stream().mapToInt(Integer::intValue).toArray(), totalLength);
    }

    /**
     * Définit comment choisir la prochaine ville à insérer dans la tournée.
     * Cette méthode doit être implémentée par les sous-classes en utilisant les distances minimales calculées.
     * @param minDistances    Tableau des distances minimales entre les villes non visitées et la tournée.
     * @param unvisitedCities La liste des villes restantes à visiter.
     * @return L'index de la ville à insérer.
     */
    protected abstract int selectCityToInsert(long[] minDistances, List<Integer> unvisitedCities);

    /**
     * Initialise la liste des villes non visitées, en excluant la ville de départ.
     *
     * @param n              Le nombre total de villes.
     * @param startCityIndex L'index de la ville de départ.
     * @return Une liste d'index représentant les villes non visitées.
     */
    private static List<Integer> initializeUnvisitedCities(int n, int startCityIndex) {
        List<Integer> unvisitedCities = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            if (i != startCityIndex) unvisitedCities.add(i);
        }
        return unvisitedCities;
    }

    /**
     * Met à jour les distances minimales des villes non visitées après l'insertion d'une nouvelle ville dans la tournée.
     * @param data            L'instance de données du TSP.
     * @param minDistances    Tableau des distances minimales pour les villes non visitées.
     * @param cityInserted    L'index de la ville qui vient d'être insérée dans la tournée.
     * @param unvisitedCities La liste des villes restantes à visiter.
     */
    private static void updateMinDistances(TspData data, long[] minDistances, int cityInserted, List<Integer> unvisitedCities) {
        for (int city : unvisitedCities) {
            long distanceToNewCity = data.getDistance(city, cityInserted);
            if (distanceToNewCity < minDistances[city]) {
                minDistances[city] = distanceToNewCity; // Met à jour si la nouvelle ville est plus proche.
            }
        }
    }

    /**
     * Trouve la meilleure position d'insertion pour une ville donnée dans la tournée actuelle.
     *
     * @param data         L'instance de données du TSP.
     * @param tour         La liste des villes déjà insérées dans la tournée.
     * @param cityToInsert L'index de la ville à insérer.
     * @return L'index dans la tournée où la ville doit être insérée pour minimiser l'augmentation de la longueur.
     */
    private static int findBestInsertionPosition(TspData data, List<Integer> tour, int cityToInsert) {
        int bestInsertPosition = 0;
        long minLengthIncrease = Long.MAX_VALUE;

        // Parcourt chaque paire consécutive de villes dans la tournée.
        for (int i = 0; i < tour.size(); i++) {
            int prevCity = tour.get(i);
            int nextCity = getNextCityInTour(tour, i);  // Gestion circulaire de la tournée.

            // Calcul de l'augmentation de la longueur si la ville est insérée entre prevCity et nextCity.
            long lengthIncrease = calculateLengthDifference(data, prevCity, cityToInsert, nextCity);

            // Si l'augmentation de la longueur est inférieure à celle actuellement stockée, mettre à jour les valeurs.
            if (lengthIncrease < minLengthIncrease) {
                minLengthIncrease = lengthIncrease;
                bestInsertPosition = i + 1;
            }
        }
        return bestInsertPosition;
    }

    /**
     * Calcule l'augmentation de la longueur de la tournée si une ville est insérée à une position donnée.
     *
     * @param data         L'instance de données du TSP.
     * @param tour         La liste des villes déjà insérées dans la tournée.
     * @param cityToInsert L'index de la ville à insérer.
     * @param position     La position où la ville doit être insérée.
     * @return L'augmentation de la longueur de la tournée.
     */
    private static long calculateLengthIncrease(TspData data, List<Integer> tour, int cityToInsert, int position) {
        int prevCity = (position == 0) ? tour.getLast() : tour.get(position - 1);
        int nextCity = tour.get(position % tour.size());
        return calculateLengthDifference(data, prevCity, cityToInsert, nextCity);
    }

    /**
     * Récupère la ville suivante dans la tournée en tenant compte du caractère circulaire de la tournée.
     *
     * @param tour        La liste des villes dans la tournée.
     * @param currentIndex L'index actuel dans la tournée.
     * @return L'index de la ville suivante dans la tournée.
     */
    private static int getNextCityInTour(List<Integer> tour, int currentIndex) {
        return tour.get((currentIndex + 1) % tour.size());
    }

    /**
     * Convertit la tournée en un ensemble d'arêtes pour visualiser le tour.
     *
     * @param tour La liste des villes dans la tournée.
     * @return Un itérateur d'arêtes représentant la tournée actuelle.
     */
    private static Iterator<Edge> tourToEdges(List<Integer> tour) {
        List<Edge> edges = new ArrayList<>();
        // Parcourt toutes les villes et crée des arêtes entre chaque paire consécutive de villes.
        for (int i = 0; i < tour.size(); i++) {
            edges.add(new Edge(tour.get(i), tour.get((i + 1) % tour.size())));  // La tournée est circulaire.
        }
        return edges.iterator();
    }

    private static long calculateLengthDifference(TspData data, int prevCity, int cityToInsert, int nextCity) {
        return data.getDistance(prevCity, cityToInsert) +
                data.getDistance(cityToInsert, nextCity) -
                data.getDistance(prevCity, nextCity);
    }
}

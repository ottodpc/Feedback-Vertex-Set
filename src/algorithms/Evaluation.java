package algorithms;
import java.awt.Point;
import java.util.*;

public class Evaluation {
  private final Map<String, ArrayList<Point>> neighborCache;
  private final Set<Point> visitedPointsCache;

  public Evaluation() {
    this.neighborCache = new HashMap<>();
    this.visitedPointsCache = new HashSet<>();
  }

  /**
   * Vérifie si un point existe dans une liste de points
   * @param points Liste des points à vérifier
   * @param p Point à rechercher
   * @return true si le point existe, false sinon
   */
  private boolean isMember(ArrayList<Point> points, Point p) { 
    if (visitedPointsCache.contains(p)) {
      return true;
    }

    for (Point point : points) {
      if (point.equals(p)) {
        visitedPointsCache.add(p);
        return true;
      }
    }
    return false;
  }

  /**
   * Vérifie si le FVS proposé est valide
   * @param origPoints Points originaux du graphe
   * @param fvs FVS proposé
   * @param edgeThreshold Seuil de distance pour les arêtes
   * @return true si le FVS est valide, false sinon
   */
  public boolean isValid(ArrayList<Point> origPoints, ArrayList<Point> fvs, int edgeThreshold) {
    neighborCache.clear();
    visitedPointsCache.clear();

    ArrayList<Point> vertices = new ArrayList<>();
    for (Point p : origPoints) {
      if (!isMember(fvs, p)) {
        vertices.add((Point)p.clone());
      }
    }

    return !hasCycle(vertices, edgeThreshold);
  }

  /**
   * Vérifie si le graphe contient un cycle
   * @param vertices Liste des sommets
   * @param edgeThreshold Seuil de distance
   * @return true si un cycle est trouvé, false sinon
   */
  private boolean hasCycle(ArrayList<Point> vertices, int edgeThreshold) { 

    // Parcours en profondeur depuis chaque sommet non visité
    while (!vertices.isEmpty()) {
      ArrayList<Point> green = new ArrayList<>();
      green.add((Point)vertices.get(0).clone());
      ArrayList<Point> black = new ArrayList<>();

      while (!green.isEmpty()) {
        Point current = green.get(0);
        ArrayList<Point> neighbors = neighbor(current, vertices, edgeThreshold);

        for (Point p : neighbors) {
          if (current.equals(p)) {
            continue;
          }

          if (isMember(black, p)) {
            return true;
          }

          if (isMember(green, p)) {
            return true;
          }

          green.add((Point)p.clone());
        }

        black.add((Point)green.get(0).clone());
        vertices.remove(green.get(0));
        green.remove(0);
      }
    }

    return false;
  }

  /**
   * Trouve les voisins d'un point dans le graphe
   * @param p Point dont on cherche les voisins
   * @param vertices Liste des sommets du graphe
   * @param edgeThreshold Seuil de distance
   * @return Liste des voisins
   */
  private ArrayList<Point> neighbor(Point p, ArrayList<Point> vertices, int edgeThreshold) {
    String key = p.x + "," + p.y + "_" + edgeThreshold;
    if (neighborCache.containsKey(key)) {
      return new ArrayList<>(neighborCache.get(key));
    }

    ArrayList<Point> result = new ArrayList<>();
    double thresholdSquared = edgeThreshold * edgeThreshold;

    for (Point point : vertices) {
      if (!point.equals(p)) {
        if (distanceSquared(point, p) < thresholdSquared) {
          result.add((Point)point.clone());
        }
      }
    }

    neighborCache.put(key, new ArrayList<>(result));
    return result;
  }

  /**
   * Calcule le carré de la distance entre deux points
   * @param p1 Premier point
   * @param p2 Second point
   * @return Le carré de la distance euclidienne
   */
  private double distanceSquared(Point p1, Point p2) {
    double dx = p1.x - p2.x;
    double dy = p1.y - p2.y;
    return dx * dx + dy * dy;
  }
}
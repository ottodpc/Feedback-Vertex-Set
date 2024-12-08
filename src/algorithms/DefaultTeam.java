package algorithms;

import java.awt.Point;
import java.util.*;

public class DefaultTeam {

  public ArrayList<Point> calculFVS(ArrayList<Point> points, int edgeThreshold) { 
    ArrayList<Point> result = new ArrayList<>(points);

    for (int i = 0; i < 3; i++) {
      ArrayList<Point> fvs = localSearch(greedy(points, edgeThreshold), points, edgeThreshold);
      System.out.println("DefaultTeam MAIN. Current sol: " + result.size() + ". Found next sol: " + fvs.size());
      if (fvs.size() < result.size()) result = fvs;
    }

    return result;
  }

  private ArrayList<Point> greedy(ArrayList<Point> pointsIn, int edgeThreshold) {
    ArrayList<Point> points = new ArrayList<>(pointsIn);
    ArrayList<Point> result = new ArrayList<>(pointsIn);

    for (int i = 0; i < 50; i++) {
      Collections.shuffle(points, new Random(System.nanoTime()));
      ArrayList<Point> rest = removeDuplicates(points);
      ArrayList<Point> fvs = new ArrayList<>();

      while (!isSolution(fvs, points, edgeThreshold)) {
        Point choosenOne = rest.get(0);
        for (Point p : rest) {
          if (degree(p, rest, edgeThreshold) > degree(choosenOne, rest, edgeThreshold)) {
            choosenOne = p;
          }
        }
        fvs.add(choosenOne);
        rest.remove(choosenOne);
      }

      if (fvs.size() < result.size()) {
        result = new ArrayList<>(fvs);
      }
    }

    return result;
  }

  private ArrayList<Point> localSearch(ArrayList<Point> firstSolution, ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> current = removeDuplicates(firstSolution);
    ArrayList<Point> next = new ArrayList<>(current);

    System.out.println("LS. First sol: " + current.size());

    do {
      current = new ArrayList<>(next);
      next = remove2add1(current, points, edgeThreshold);
      System.out.println("LS. Current sol: " + current.size() + ". Found next sol: " + next.size());
    } while (score(current) > score(next));

    return next;
  }

  private ArrayList<Point> remove2add1(ArrayList<Point> candidate, ArrayList<Point> points, int edgeThreshold) {
    ArrayList<Point> test = removeDuplicates(candidate);
    ArrayList<Point> rest = removeDuplicates(points);
    rest.removeAll(test);

    for (int i = 0; i < test.size(); i++) {
      for (int j = i + 1; j < test.size(); j++) {
        Point p = test.remove(i);
        Point q = test.remove(j - 1);

        for (Point r : rest) {
          test.add(r);
          if (isSolution(test, points, edgeThreshold)) {
            return new ArrayList<>(test);
          }
          test.remove(r);
        }

        test.add(i, p);
        test.add(j, q);
      }
    }

    return candidate;
  }

  private boolean isSolution(ArrayList<Point> candidateIn, ArrayList<Point> pointsIn, int edgeThreshold) {
    ArrayList<Point> candidate = removeDuplicates(candidateIn);
    ArrayList<Point> rest = removeDuplicates(pointsIn);
    rest.removeAll(candidate);
    ArrayList<Point> visited = new ArrayList<>();

    while (!rest.isEmpty()) {
      visited.clear();
      visited.add(rest.remove(0));

      for (int i = 0; i < visited.size(); i++) {
        for (Point p : new ArrayList<>(rest)) {
          if (isEdge(visited.get(i), p, edgeThreshold)) {
            for (Point q : visited) {
              if (!q.equals(visited.get(i)) && isEdge(p, q, edgeThreshold)) {
                return false;
              }
            }
            visited.add(p);
            rest.remove(p);
          }
        }
      }
    }

    return true;
  }

  private ArrayList<Point> removeDuplicates(ArrayList<Point> points) {
    ArrayList<Point> result = new ArrayList<>();
    for (Point p : points) {
      if (!result.contains(p)) {
        result.add((Point)p.clone());
      }
    }
    return result;
  }

  private boolean isEdge(Point p, Point q, int edgeThreshold) {
    return p.distance(q) < edgeThreshold;
  }

  private int degree(Point p, ArrayList<Point> points, int edgeThreshold) {
    int degree = 0;
    for (Point q : points) {
      if (!p.equals(q) && isEdge(p, q, edgeThreshold)) {
        degree++;
      }
    }
    return degree;
  }

  private int score(ArrayList<Point> candidate) {
    return candidate.size();
  }
}
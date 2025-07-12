package com.example.transporters.service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class CombinationService {
    public static List<List<Integer>> generateSortedCombinations(List<Integer> input, int size) {
        List<List<Integer>> result = new ArrayList<>();
        Collections.sort(input); // Ensure input is sorted
        backtrack(input, size, 0, new ArrayList<>(), result);
        return result;
    }

    private static void backtrack(List<Integer> input, int size, int start, List<Integer> current, List<List<Integer>> result) {
        if (current.size() == size) {
            result.add(new ArrayList<>(current));
            return;
        }

        for (int i = start; i < input.size(); i++) {
            current.add(input.get(i));
            backtrack(input, size, i + 1, current, result); // i + 1 to avoid duplicates and ensure order
            current.remove(current.size() - 1); // Backtrack
        }
    }
}

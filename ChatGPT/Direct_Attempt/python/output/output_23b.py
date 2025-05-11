# ChatGPT-generated solution will go here.
from collections import defaultdict
from itertools import combinations

def main():
    with open("input.txt") as f:
        edges = [line.strip().split('-') for line in f if line.strip()]

    graph = defaultdict(set)
    for a, b in edges:
        graph[a].add(b)
        graph[b].add(a)

    nodes = list(graph.keys())
    max_clique = []

    def is_clique(clique):
        return all(b in graph[a] for a, b in combinations(clique, 2))

    def dfs(clique, candidates):
        nonlocal max_clique
        if len(clique) > len(max_clique):
            max_clique = clique[:]
        for i, node in enumerate(candidates):
            new_clique = clique + [node]
            new_candidates = [n for n in candidates[i+1:] if all(n in graph[x] for x in new_clique)]
            dfs(new_clique, new_candidates)

    dfs([], nodes)
    print(','.join(sorted(max_clique)))

if __name__ == "__main__":
    main()

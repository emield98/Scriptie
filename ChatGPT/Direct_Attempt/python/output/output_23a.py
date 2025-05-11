# ChatGPT-generated solution will go here.
import itertools
from collections import defaultdict

def main():
    with open("input.txt") as f:
        edges = [line.strip().split('-') for line in f if line.strip()]

    graph = defaultdict(set)
    for a, b in edges:
        graph[a].add(b)
        graph[b].add(a)

    count = 0
    nodes = list(graph.keys())

    for a, b, c in itertools.combinations(nodes, 3):
        if b in graph[a] and c in graph[a] and a in graph[b] and c in graph[b] and a in graph[c] and b in graph[c]:
            if a.startswith('t') or b.startswith('t') or c.startswith('t'):
                count += 1

    print(count)

if __name__ == "__main__":
    main()

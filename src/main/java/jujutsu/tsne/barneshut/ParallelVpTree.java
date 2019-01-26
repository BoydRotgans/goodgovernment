package jujutsu.tsne.barneshut;

import jujutsu.tsne.barneshut.DataPoint;
import jujutsu.tsne.barneshut.Distance;
import jujutsu.tsne.barneshut.VpTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.Callable;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.Future;

public class ParallelVpTree<StorageType> extends jujutsu.tsne.barneshut.VpTree<StorageType> {

	private ForkJoinPool searcherPool;
	
	public ParallelVpTree(ForkJoinPool pool, Distance distance) {
		super(distance);
		searcherPool = pool;
	}
	
	public ParallelVpTree(ForkJoinPool pool) {
		searcherPool = pool;
	}
	
	public List<Future<ParallelTreeNode.TreeSearchResult>> searchMultiple(ParallelVpTree<StorageType> tree, jujutsu.tsne.barneshut.DataPoint[] targets, int k) {
		List<ParallelTreeNode.ParallelTreeSearcher> searchers = new ArrayList<>();
		for(int n = 0; n < targets.length; n++) {
			@SuppressWarnings("unchecked")
			ParallelTreeNode node = (ParallelTreeNode) tree.getRoot();
			searchers.add(node.new ParallelTreeSearcher(node,_items,targets[n], k, n));
		}
		List<Future<ParallelTreeNode.TreeSearchResult>> results =  searcherPool.invokeAll(searchers);
		return results;
	}

	@Override
	protected jujutsu.tsne.barneshut.VpTree.Node createNode() {
		return new ParallelTreeNode();
	}

	class ParallelTreeNode extends VpTree.Node {
		
		class TreeSearchResult {
			int n;
			List<Double> distances;
			List<jujutsu.tsne.barneshut.DataPoint> indices;
			
			public TreeSearchResult(List<jujutsu.tsne.barneshut.DataPoint> indices, List<Double> distances, int n) {
				this.indices = indices;
				this.distances = distances;
				this.n = n;
			}

			public List<jujutsu.tsne.barneshut.DataPoint> getIndices() {
				return indices;
			}

			public List<Double> getDistances() {
				return distances;
			}

			
			public int getIndex() {
				return n;
			}

		}

		class ParallelTreeSearcher implements Callable<TreeSearchResult> {
			VpTree.Node node;
			Queue<HeapItem> heap;
			jujutsu.tsne.barneshut.DataPoint target;
			int k;
			int n;
			jujutsu.tsne.barneshut.DataPoint[] items;

			public ParallelTreeSearcher(VpTree.Node tree, jujutsu.tsne.barneshut.DataPoint[] items, jujutsu.tsne.barneshut.DataPoint target, int k, int n) {
				this.node = tree;
				this.target = target;
				this.k = k;
				this.items = items;
				this.n = n;
			}

			@Override
			public TreeSearchResult call() {
				List<DataPoint> indices = new ArrayList<>();
				List<Double> distances = new ArrayList<>();
				PriorityQueue<HeapItem> heap = new PriorityQueue<HeapItem>(k,new Comparator<HeapItem>() {
					@Override
					public int compare(HeapItem o1, HeapItem o2) {
						return -1 * o1.compareTo(o2);
					}
				}); 

				double tau = Double.MAX_VALUE;
				// Perform the search
				node.search(node, target, k, heap, tau);

				// Gather final results
				while(!heap.isEmpty()) {
					indices.add(items[heap.peek().index]);
					distances.add(heap.peek().dist);
					heap.remove();
				}
				
				// Results are in reverse order 
				Collections.reverse(indices);
				Collections.reverse(distances);

				return new TreeSearchResult(indices, distances,n);
			}   
		}
	}
}

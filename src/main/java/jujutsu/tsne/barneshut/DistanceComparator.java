package jujutsu.tsne.barneshut;

import jujutsu.tsne.barneshut.DataPoint;
import jujutsu.tsne.barneshut.Distance;
import jujutsu.tsne.barneshut.EuclideanDistance;

import java.util.Comparator;

public class DistanceComparator implements Comparator<jujutsu.tsne.barneshut.DataPoint> {
	jujutsu.tsne.barneshut.DataPoint refItem;
	jujutsu.tsne.barneshut.Distance dist;
	
	DistanceComparator(jujutsu.tsne.barneshut.DataPoint refItem) {
		this.refItem = refItem;
		this.dist = new EuclideanDistance();
	}
	
	DistanceComparator(jujutsu.tsne.barneshut.DataPoint refItem, Distance dist) {
		this.refItem = refItem;
		this.dist = dist;
	}

	@Override
	public int compare(jujutsu.tsne.barneshut.DataPoint o1, DataPoint o2) {
		return dist.distance(o1, refItem) < dist.distance(o2, refItem) ? -1 :
			(dist.distance(o1, refItem) > dist.distance(o2, refItem) ? 1 : 0);
	}
}
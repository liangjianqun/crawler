package crawler.common;

public class Pair<L, R> {
	private final L first_;
	private final R second_;

	public Pair(L first, R second) {
		this.first_ = first;
		this.second_ = second;
	}

	public L first() {
		return first_;
	}

	public R second() {
		return second_;
	}

	public int hashCode() {
		return first_.hashCode() ^ second_.hashCode();
	}

	public boolean equals(Object o) {
		if (!(o instanceof Pair))
			return false;
		Pair pairo = (Pair) o;
		return this.first_.equals(pairo.first())
				&& this.second_.equals(pairo.second());
	}

}
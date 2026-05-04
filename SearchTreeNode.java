import java.util.Optional;

public interface SearchTreeNode<T extends Comparable<T>> {
    SearchTreeNode<T> add(final T value);
    Optional<?extends SearchTreeNode<T>> remove(final T value);
    T getMin();

}



import java.util.*;
import java.util.stream.*;

public class BinaryTreeNode<T extends Comparable<T>> implements SearchTreeNode<T> {

    protected static <T extends Comparable<T>> int height(final Optional<?extends SearchTreeNode<T>> node) {
        return node.isEmpty() ? 0 : node.get().getHeight();
    }

    protected final Optional<?extends BinaryTreeNode<T>> leftChild;

    protected final Optional<?extends BinaryTreeNode<T>> rightChild;

    protected final T value;
    
    protected final BinaryTreeNodeFactory<T> nodeFactory;

    public BinaryTreeNode(final T value, final BinaryTreeNodeFactory<T> nodeFactory) {
        this(value, Optional.empty(), Optional.empty(), nodeFactory);
    }

    public BinaryTreeNode(
        final T value,
        final BinaryTreeNode<T> leftChild,
        final BinaryTreeNode<T> rightChild,
        final BinaryTreeNodeFactory<T> nodeFactory
    ) {
        this(value, Optional.of(leftChild), Optional.of(rightChild), nodeFactory);
    }

    public BinaryTreeNode(
        final T value,
        final Optional<?extends SearchTreeNode<T>> leftChild,
        final Optional<?extends SearchTreeNode<T>> rightChild,
        final BinaryTreeNodeFactory<T> nodeFactory
    ) {
        this.value = value;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.nodeFactory = nodeFactory;
    }

    public BinaryTreeNode<T> add(final T value) {
        if (this.value.compareTo(value) < 0) {
            if (this.rightChild.isEmpty()) {
                return this.setRightChild(new BinaryTreeNode<T>(value));
            }
            return this.setRightChild(this.rightChild.get().add(value));
        }
        if (this.leftChild.isEmpty()) {
            return this.setLeftChild(new BinaryTreeNode<T>(value));
        }
        return this.setLeftChild(this.leftChild.get().add(value));
    }

    public boolean containsAll(final Collection<? extends T> values) {
        if (values.isEmpty()) {
            return true;
        }
        final List<? extends T> cWithoutValue = values.stream().filter(x -> x.compareTo(this.value) != 0).toList();
        final LinkedList<? extends T> left = this.getLeft(cWithoutValue);
        final LinkedList<? extends T> right = this.getRight(cWithoutValue);
        return (left.isEmpty() || this.leftChild.isPresent() && this.leftChild.get().containsAll(left))
            && (right.isEmpty() || this.rightChild.isPresent() && this.rightChild.get().containsAll(right));
    }

    @Override
    public boolean equals(final Object o) {
        if (!(o instanceof BinaryTreeNode)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final BinaryTreeNode<T> other = (BinaryTreeNode<T>)o;
        return this.value.equals(other.value)
            && this.leftChild.equals(other.leftChild)
            && this.rightChild.equals(other.rightChild);
    }

    public int getHeight() {
        return Math.max(BinaryTreeNode.height(this.leftChild), BinaryTreeNode.height(this.rightChild)) + 1;
    }

    public T getMin() {
        BinaryTreeNode<T> current = this;
        while (current.leftChild.isPresent()) {
            current = current.leftChild.get();
        }
        return current.value;
    }

    @Override
    public int hashCode() {
        return this.value.hashCode() * 5
            + this.leftChild.hashCode() * 3
            + this.rightChild.hashCode() * 2;
    }

    public Optional<BinaryTreeNode<T>> remove(final T value) {
        final int comparison = this.value.compareTo(value);
        if (comparison == 0) {
            if (this.leftChild.isEmpty()) {
                if (this.rightChild.isEmpty()) {
                    return Optional.empty();
                }
                return Optional.of(this.rightChild.get());
            }
            if (this.rightChild.isEmpty()) {
                return Optional.of(this.leftChild.get());
            }
            final T min = this.rightChild.get().getMin();
            return Optional.of(new BinaryTreeNode<T>(min, this.leftChild, this.rightChild.get().remove(min)));
        }
        if (comparison < 0) {
            if (this.rightChild.isEmpty()) {
                return Optional.of(this);
            }
            return Optional.of(this.setRightChild(this.rightChild.get().remove(value)));
        }
        if (this.leftChild.isEmpty()) {
            return Optional.of(this);
        }
        return Optional.of(this.setLeftChild(this.leftChild.get().remove(value)));
    }

    public BinaryTreeNode<T> setLeftChild(final BinaryTreeNode<T> leftChild) {
        return this.setLeftChild(Optional.of(leftChild));
    }

    public BinaryTreeNode<T> setLeftChild(final Optional<?extends BinaryTreeNode<T>> leftChild) {
        return this.nodeFactory.create(this.value, leftChild, this.rightChild);
    }

    public SearchTreeNode<T> setRightChild(final BinaryTreeNode<T> rightChild) {
        return this.setRightChild(Optional.of(rightChild));
    }

    public SearchTreeNode<T> setRightChild(final Optional<?extends BinaryTreeNode<T>> rightChild) {
        return this.nodeFactory.create(this.value, this.leftChild, rightChild);
    }

    public BinaryTreeNode<T> setValue(final T value) {
        return new BinaryTreeNode<T>(value, this.leftChild, this.rightChild);
    }

    public int size() {
        return
            (this.leftChild.isEmpty() ? 0 : this.leftChild.get().size())
            + (this.rightChild.isEmpty() ? 0 : this.rightChild.get().size())
            + 1;
    }

    public Stream<T> stream() {
        final Stream<T> left = this.leftChild.isPresent() ? this.leftChild.get().stream() : Stream.empty();
        final Stream<T> right = this.rightChild.isPresent() ? this.rightChild.get().stream() : Stream.empty();
        return Stream.concat(Stream.concat(left, Stream.of(this.value)), right);
    }

    @Override
    public String toString() {
        return String.format(
            "(%s,%s,%s)",
            this.leftChild.isEmpty() ? "" : this.leftChild.get().toString(),
            this.value.toString(),
            this.rightChild.isEmpty() ? "" : this.rightChild.get().toString()
        );
    }

    private LinkedList<? extends T> getLeft(final Collection<? extends T> values) {
        return values.stream()
            .filter(x -> x.compareTo(this.value) <= 0)
            .collect(Collectors.toCollection(LinkedList::new));
    }

    private LinkedList<? extends T> getRight(final Collection<? extends T> values) {
        return values.stream()
            .filter(x -> x.compareTo(this.value) > 0)
            .collect(Collectors.toCollection(LinkedList::new));
    }

}

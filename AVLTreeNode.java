import java.util.*;
import java.util.stream.*;

public class AVLTreeNode<T extends Comparable<T>> {

    private static <T extends Comparable<T>> int height(final Optional<AVLTreeNode<T>> node) {
        return node.isEmpty() ? 0 : node.get().getHeight();
    }

    private final int height;

    private final Optional<AVLTreeNode<T>> leftChild;

    private final Optional<AVLTreeNode<T>> rightChild;

    private final T value;

    public AVLTreeNode(final T value) {
        this(value, Optional.empty(), Optional.empty());
    }

    public AVLTreeNode(
        final T value,
        final Optional<AVLTreeNode<T>> leftChild,
        final Optional<AVLTreeNode<T>> rightChild
    ) {
        this.value = value;
        this.leftChild = leftChild;
        this.rightChild = rightChild;
        this.height = Math.max(AVLTreeNode.height(leftChild), AVLTreeNode.height(rightChild)) + 1;
    }

    public AVLTreeNode<T> add(final T value) {
        if (this.value.compareTo(value) < 0) {
            if (this.rightChild.isEmpty()) {
                return this.setRightChild(new AVLTreeNode<T>(value));
            }
            return this.setRightChild(this.rightChild.get().add(value).balance()).balance();
        }
        if (this.leftChild.isEmpty()) {
            return this.setLeftChild(new AVLTreeNode<T>(value));
        }
        return this.setLeftChild(this.leftChild.get().add(value).balance()).balance();
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
        if (!(o instanceof AVLTreeNode)) {
            return false;
        }
        @SuppressWarnings("unchecked")
        final AVLTreeNode<T> other = (AVLTreeNode<T>)o;
        return this.value.equals(other.value)
            && this.leftChild.equals(other.leftChild)
            && this.rightChild.equals(other.rightChild);
    }

    public int getHeight() {
        return this.height;
    }

    public T getMin() {
        AVLTreeNode<T> current = this;
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

    public Optional<AVLTreeNode<T>> remove(final T value) {
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
            final Optional<AVLTreeNode<T>> newRight = this.rightChild.get().remove(min);
            return Optional.of(
                new AVLTreeNode<T>(
                    min,
                    this.leftChild,
                    newRight.isEmpty() ? newRight : Optional.of(newRight.get().balance())
                ).balance()
            );
        }
        if (comparison < 0) {
            if (this.rightChild.isEmpty()) {
                return Optional.of(this);
            }
            final Optional<AVLTreeNode<T>> newRight = this.rightChild.get().remove(value);
            return Optional.of(
                this.setRightChild(newRight.isEmpty() ? newRight : Optional.of(newRight.get().balance())).balance()
            );
        }
        if (this.leftChild.isEmpty()) {
            return Optional.of(this);
        }
        final Optional<AVLTreeNode<T>> newLeft = this.leftChild.get().remove(value);
        return Optional.of(
            this.setLeftChild(newLeft.isEmpty() ? newLeft : Optional.of(newLeft.get().balance())).balance()
        );
    }

    public AVLTreeNode<T> setLeftChild(final AVLTreeNode<T> leftChild) {
        return this.setLeftChild(Optional.of(leftChild));
    }

    public AVLTreeNode<T> setLeftChild(final Optional<AVLTreeNode<T>> leftChild) {
        return new AVLTreeNode<T>(this.value, leftChild, this.rightChild);
    }

    public AVLTreeNode<T> setRightChild(final AVLTreeNode<T> rightChild) {
        return this.setRightChild(Optional.of(rightChild));
    }

    public AVLTreeNode<T> setRightChild(final Optional<AVLTreeNode<T>> rightChild) {
        return new AVLTreeNode<T>(this.value, this.leftChild, rightChild);
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

    private AVLTreeNode<T> balance() {
        final int diff = this.leftHeight() - this.rightHeight();
        if (diff < -1) {
            return this.balanceRightToLeft();
        } else if (diff > 1) {
            return this.balanceLeftToRight();
        } else {
            return this;
        }
    }

    private AVLTreeNode<T> balanceLeftToRight() {
        final AVLTreeNode<T> left = this.leftChild.get();
        if (
            left.rightChild.isEmpty() ||
            (left.leftChild.isPresent() && left.rightChild.get().getHeight() <= left.leftChild.get().getHeight())
        ) {
            return this.rotateRight();
        }
        return this.setLeftChild(left.rotateLeft()).rotateRight();
    }

    private AVLTreeNode<T> balanceRightToLeft() {
        final AVLTreeNode<T> right = this.rightChild.get();
        if (
            right.leftChild.isEmpty() ||
            (right.rightChild.isPresent() && right.leftChild.get().getHeight() <= right.rightChild.get().getHeight())
        ) {
            return this.rotateLeft();
        }
        return this.setRightChild(right.rotateRight()).rotateLeft();
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

    private int leftHeight() {
        return AVLTreeNode.height(this.leftChild);
    }

    private int rightHeight() {
        return AVLTreeNode.height(this.rightChild);
    }

    private AVLTreeNode<T> rotateLeft() {
        return this.rightChild.get().setLeftChild(this.setRightChild(this.rightChild.get().leftChild));
    }

    private AVLTreeNode<T> rotateRight() {
        return this.leftChild.get().setRightChild(this.setLeftChild(this.leftChild.get().rightChild));
    }

}

import java.util.*;
import java.util.stream.*;

public class AVLTreeNode<T extends Comparable<T>> extends BinaryTreeNode<T> {

    private final int height;

    public AVLTreeNode(final T value) {
        this(value, Optional.empty(), Optional.empty());
    }

    public AVLTreeNode(
        final T value,
        final Optional<AVLTreeNode<T>> leftChild,
        final Optional<AVLTreeNode<T>> rightChild
    ) {
        super(value, leftChild, rightChild);
        this.height = Math.max(BinaryTreeNode.height(leftChild), AVLTreeNode.height(rightChild)) + 1;
    }

    public AVLTreeNode<T> add(final T value) {
        if (this.value.compareTo(value) < 0) {
            if (this.rightChild.isEmpty()) {
                return this.setRightChild(new AVLTreeNode<T>(value));
            }
            return this.setRightChild(((AVLTreeNode<T>) this.rightChild.get().add(value)).balance()).balance();
        }
        if (this.leftChild.isEmpty()) {
            return this.setLeftChild(new AVLTreeNode<T>(value));
        }
        return this.setLeftChild(((AVLTreeNode<T>)this.leftChild.get().add(value)).balance()).balance();
    }

    public int getHeight() {
        return this.height;
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

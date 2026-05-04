import java.util.Optional;

public class BinaryTreeNodeFactory <T extends Comparable<T>>{

    public BinaryTreeNode<T> create(final T value, final Optional<?extends BinaryTreeNode<T>> leftChild, final Optional<?extends BinaryTreeNode<T>> rightChild) {
        return new BinaryTreeNode<T>(value, leftChild, rightChild, this);
    }

}

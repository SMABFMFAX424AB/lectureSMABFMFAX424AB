import java.util.*;

public class ImmutableSet<T> implements Iterable<T> {

    private static class ImmutableSetNode<T>{
        final T element;
        final ImmutableSetNode<T> next;
        final boolean added;

        ImmutableSetNode(T element, ImmutableSetNode<T> next, boolean added) {
            this.element = element;
            this.next = next;
            this.added = added;
        }
    }

    final ImmutableSetNode<T> root;

    public ImmutableSet(){
        this.root = null;
    }

    public ImmutableSet(Collection<T> c){
        if(c == null || c.isEmpty()){
            this.root = null;
        }else{
            Iterator<T> iterator = c.iterator();
            ImmutableSetNode<T> current = new ImmutableSetNode<T>(iterator.next(), null, true);
            while(iterator.hasNext()){
                current = new ImmutableSetNode<T>(iterator.next(), current, true);
            }
            root = current;
        }
    }

    private ImmutableSet(ImmutableSetNode<T> root){
        this.root = root;
    }

    public int size() {
        // TODO Auto-generated method stub
        return 0;
    }

    public boolean isEmpty() {
        return root == null;
    }

    public boolean contains(Object o) {
        for(T t : this) {
            if(t.equals(o)){
                return true;
            }
        }
        /*
            Iterator<T> iterator = this.iterator();
            while(iterator.hasNext()) {
                T t = iterator.next();
                Gleicher Schleifenkörper wie oben
            }
        */
        return false;
        /*ImmutableSetNode<T> current = root;
        while(current != null) {
            if (current.element.equals(o)){
                return current.added;
            }
            current = current.next;
        }
        return false;*/
    }

    public static class ImmutableSetIterator<T> implements Iterator<T> {
        ImmutableSetNode<T> current;
        ImmutableSet<T> used;

        public ImmutableSetIterator(ImmutableSetNode<T> current) {
            this.current = current;
            used = new ImmutableSet<>();
            forward();
        }

        @Override
        public boolean hasNext() {
            return current != null;
        }

        @Override
        public T next() {
            T result = current.element;
            used = used.add(current.element);
            current = current.next;
            forward();
            return result;
        }

        private void forward() {
            while(current != null && (!current.added || !used.contains(current.element))){
                used = used.add(current.element);
                current = current.next;
            }
        }
    }

    @Override
    public Iterator<T> iterator() {
        return new ImmutableSetIterator<>(root);
    }

    public ImmutableSet<T> add(T e) {
        return new ImmutableSet<T>(new ImmutableSetNode<T>(e, this.root, true));
    }

    public ImmutableSet<T> remove(T e) {
        return new ImmutableSet<T>(new ImmutableSetNode<T>(e, this.root, false));
    }

    public boolean containsAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean addAll(Collection<? extends T> c) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean retainAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    public boolean removeAll(Collection<?> c) {
        // TODO Auto-generated method stub
        return false;
    }

    public ImmutableSet<T> clear() {
        return new ImmutableSet<T>();
    }


}

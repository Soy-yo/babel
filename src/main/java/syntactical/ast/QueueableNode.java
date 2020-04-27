package syntactical.ast;

import java.util.Iterator;

public abstract class QueueableNode<N extends QueueableNode<N>> extends ASTNode implements Iterable<N> {

    protected N previous;
    protected N next;

    protected abstract N self();

    public boolean hasNext() {
        return next != null;
    }

    public N getNext() {
        return next;
    }

    public boolean hasPrevious() {
        return previous != null;
    }

    public N getPrevious() {
        return previous;
    }

    public N linkedTo(N next) {
        checkNext(this);
        if (next == null) {
            this.next = null;
            return self();
        }
        checkPrevious(next);
        this.next = next;
        next.previous = self();
        return self();
    }

    public N unlinked() {
        N temp = next;
        if (next != null) {
            next.previous = previous;
            next = null;
        }
        if (previous != null) {
            previous.next = temp;
            previous = null;
        }
        return self();
    }

    public N linkedBefore(N node) {
        checkNext(this);
        checkPrevious(this);
        N p = node.previous;
        next = node;
        node.previous = self();
        previous = p;
        if (p != null) {
            p.next = self();
        }
        return self();
    }

    public N linkedAfter(N node) {
        checkNext(this);
        checkPrevious(this);
        N n = node.next;
        next = n;
        node.next = self();
        previous = node;
        if (n != null) {
            n.previous = self();
        }
        return self();
    }

    @Override
    public Iterator<N> iterator() {
        return new Iterator<N>() {
            private N result = self();

            @Override
            public boolean hasNext() {
                return result != null;
            }

            @Override
            public N next() {
                N ret = result;
                result = result.next;
                return ret;
            }
        };
    }

    private static <N extends QueueableNode<N>> void checkNext(QueueableNode<N> node) {
        if (node.next != null) {
            throw new IllegalArgumentException(node + " already has a next node");
        }
    }

    private static <N extends QueueableNode<N>> void checkPrevious(QueueableNode<N> node) {
        if (node.previous != null) {
            throw new IllegalArgumentException(node + " already has a previous node");
        }
    }

}

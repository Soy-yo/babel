package syntactical.ast;

abstract class QueueableNode<N extends QueueableNode<N>> extends ASTNode {

    protected N previous;
    protected N next;

    protected abstract N self();

    public N getNext() {
        return next;
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

    private static <N extends QueueableNode<N>> void checkNext(QueueableNode<N> node) {
        if (node.next != null) {
            throw new IllegalArgumentException(node + " already has a next node");
        }
    }

    private static <N extends QueueableNode<N>> void checkPrevious(QueueableNode<N> node) {
        if (node.next != null) {
            throw new IllegalArgumentException(node + " already has a previous node");
        }
    }

}

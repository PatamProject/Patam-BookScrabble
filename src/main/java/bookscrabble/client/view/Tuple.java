package bookscrabble.client.view;

public class Tuple<A,B,C> {
    private final A first;
    private final B second;
    private final C third;

    public Tuple(A first, B second, C third){
        this.first = first;
        this.second = second;
        this.third = third;
    }

    public A getFirst(){return first;}
    public B getSecond(){return second;}
    public C getThird(){return third;}

    @Override
    public String toString() {
        return "Tuple{" +
                "first=" + first +
                ", second=" + second +
                ", third=" + third +
                '}';
    }
}

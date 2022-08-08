package top.kwseeker.netty.clazz.inner;

public class Outer {

    String attr;

    public Outer(String attr) {
        this.attr = attr;
    }

    public void print() {
        System.out.println(attr);
    }

    public static void main(String[] args) {
        Inner inner = new Outer("hello").new Inner();
        Outer outer = inner.getOuterClass();
        outer.print();
    }

    final class Inner {

        public Outer getOuterClass() {
            return Outer.this;
        }
    }
}

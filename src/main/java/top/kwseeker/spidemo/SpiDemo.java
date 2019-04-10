package top.kwseeker.spidemo;

public class SpiDemo {

    public static void main(String[] args) {
        DemoService demoService = DemoServiceImplObserver.getDemoServiceImpl();
        System.out.println(demoService.sayHi());
    }
}

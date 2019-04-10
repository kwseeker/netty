package top.kwseeker.spidemo;

import org.yaml.snakeyaml.Yaml;
import top.kwseeker.spidemo.impl.EnglishDemoServiceImpl;

import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;
import java.util.ServiceLoader;
import java.util.stream.StreamSupport;

class DemoServiceImplObserver {

    private static Map<String, Object> settings = null;
    private static final String filePath = "spi.yml";

    @SuppressWarnings("unchecked")
    private static Map<String, Object> getInstance() {
        if(settings == null) {
            synchronized (DemoServiceImplObserver.class) {
                if(settings == null) {
                    Yaml yaml = new Yaml();
                    InputStream in = DemoServiceImplObserver.class.getClassLoader().getResourceAsStream(filePath);
                    settings = yaml.loadAs(in, Map.class);
                }
            }
        }
        return settings;
    }

    public static DemoService getDemoServiceImpl() {
        ServiceLoader<DemoService> demoServices = ServiceLoader.load(DemoService.class);
        Iterator<DemoService> iterator = demoServices.iterator();
        if(!iterator.hasNext()) {
            return new EnglishDemoServiceImpl();
        }

        //这里实现一种规则（可以是先遵循配置文件，再遵循默认文件）用于从实现列表中选取一种实现
        settings = getInstance();
        if(settings != null && settings.size() > 0) {
            //选取配置文件中设置的实现
            String targetImpl = "top.kwseeker.spidemo.impl." + (String) settings.get("demoServiceImpl");
            for(DemoService demoService : demoServices) {
                if(demoService.getClass().getName().equals(targetImpl)) {
                    return demoService;
                }
            }
        }
        //选取默认的实现(默认选择第一个)
        return StreamSupport.stream(demoServices.spliterator(),false).findFirst().get();
    }
}

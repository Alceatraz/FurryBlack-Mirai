package studio.blacktech.furryblackplus.test;

import studio.blacktech.furryblackplus.core.annotation.Component;
import studio.blacktech.furryblackplus.core.define.PluginPackage;
import studio.blacktech.furryblackplus.core.interfaces.AbstractEventHandler;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class SchemaModule {

    private List<PluginPackage> records;


    public int size() {
        return this.records.size();
    }


    public void checkConflict() {

        if (this.records.isEmpty()) {
            return;
        }

        Set<String> artificialRegistry = new HashSet<>();

        for (PluginPackage record : this.records) {

            for (Class<? extends AbstractEventHandler> clazz : record.getModules().values()) {

                Component annotation = clazz.getAnnotation(Component.class);

                String artificial = annotation.artificial();

                if (artificialRegistry.contains(artificial)) {


                } else {

                }

            }

        }

    }


    public static class Record {

        private String pluginName;
        private Component annotation;
        private ModuleType moduleType;
        private Class<? extends AbstractEventHandler> clazz;
        private Wrapper<? extends AbstractEventHandler> wrapper;

        public String getPluginName() {
            return this.pluginName;
        }

        public void setPluginName(String pluginName) {
            this.pluginName = pluginName;
        }

        public Component getAnnotation() {
            return this.annotation;
        }

        public void setAnnotation(Component annotation) {
            this.annotation = annotation;
        }

        public ModuleType getModuleType() {
            return this.moduleType;
        }

        public void setModuleType(ModuleType moduleType) {
            this.moduleType = moduleType;
        }

        public Class<? extends AbstractEventHandler> getClazz() {
            return this.clazz;
        }

        public void setClazz(Class<? extends AbstractEventHandler> clazz) {
            this.clazz = clazz;
        }

        public Wrapper<? extends AbstractEventHandler> getWrapper() {
            return this.wrapper;
        }

        public void setWrapper(Wrapper<? extends AbstractEventHandler> wrapper) {
            this.wrapper = wrapper;
        }
    }


    public enum ModuleType {

        RUNNER,
        FILTER,
        MONITOR,
        EXECUTOR,

        ;

    }

    @SuppressWarnings("unused")
    public static class Wrapper<T extends AbstractEventHandler> {

        private final ReadWriteLock lock;

        private T instance;

        public Wrapper(T instance) {
            this.instance = instance;
            this.lock = new ReentrantReadWriteLock();
        }

        public T getInstance() {
            try {
                this.lock.readLock().lock();
                return this.instance;
            } finally {
                this.lock.readLock().unlock();
            }
        }

        public void setInstance(T instance) {
            try {
                this.lock.writeLock().lock();
                this.instance = instance;
            } finally {
                this.lock.writeLock().unlock();
            }
        }
    }

}

package studio.blacktech.furryblackplus.core.schema;

import studio.blacktech.furryblackplus.core.annotation.Executor;
import studio.blacktech.furryblackplus.core.annotation.Filter;
import studio.blacktech.furryblackplus.core.annotation.Monitor;
import studio.blacktech.furryblackplus.core.annotation.Runner;
import studio.blacktech.furryblackplus.core.define.moduel.AbstractEventHandler;
import studio.blacktech.furryblackplus.core.define.moduel.EventHandlerExecutor;
import studio.blacktech.furryblackplus.core.exception.moduels.scan.ScanException;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerFilter;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerMonitor;
import studio.blacktech.furryblackplus.core.interfaces.EventHandlerRunner;
import studio.blacktech.furryblackplus.core.schema.element.Plugin;
import studio.blacktech.furryblackplus.core.utilties.LoggerX;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListMap;

public class Schema {

    private final LoggerX logger = new LoggerX(Schema.class);
    private final File folder;

    private final Map<String, Class<? extends AbstractEventHandler>> modules;

    private final Map<Runner, Class<? extends EventHandlerRunner>> runnerClassMap;
    private final Map<Filter, Class<? extends EventHandlerFilter>> filterClassMap;
    private final Map<Monitor, Class<? extends EventHandlerMonitor>> monitorClassMap;
    private final Map<Executor, Class<? extends EventHandlerExecutor>> executorClassMap;

    private final Map<Runner, EventHandlerRunner> runnerInstanceMap;
    private final Map<Filter, EventHandlerFilter> filterInstanceMap;
    private final Map<Monitor, EventHandlerMonitor> monitorInstanceMap;
    private final Map<Executor, EventHandlerExecutor> executorInstanceMap;

    public Schema(File folder) {

        this.folder = folder;

        this.modules = new LinkedHashMap<>();

        this.runnerClassMap = new ConcurrentSkipListMap<>((o1, o2) -> {
            if (Objects.equals(o1, o2)) {
                return 0;
            }
            int i = o1.priority() - o2.priority();
            if (i == 0) {
                return 1;
            } else {
                return i;
            }
        });
        this.filterClassMap = new ConcurrentSkipListMap<>((o1, o2) -> {
            if (Objects.equals(o1, o2)) {
                return 0;
            }
            int i = o1.priority() - o2.priority();
            if (i == 0) {
                return 1;
            } else {
                return i;
            }
        });
        this.monitorClassMap = new ConcurrentSkipListMap<>((o1, o2) -> {
            if (Objects.equals(o1, o2)) {
                return 0;
            }
            int i = o1.priority() - o2.priority();
            if (i == 0) {
                return 1;
            } else {
                return i;
            }
        });
        this.executorClassMap = new ConcurrentHashMap<>();

        this.runnerInstanceMap = new ConcurrentSkipListMap<>((o1, o2) -> {
            if (o1 == o2) {
                return 0;
            }
            int i = o1.priority() - o2.priority();
            if (i == 0) {
                return 1;
            } else {
                return i;
            }
        });
        this.filterInstanceMap = new ConcurrentSkipListMap<>((o1, o2) -> {
            if (o1 == o2) {
                return 0;
            }
            int i = o1.priority() - o2.priority();
            if (i == 0) {
                return 1;
            } else {
                return i;
            }
        });
        this.monitorInstanceMap = new ConcurrentSkipListMap<>((o1, o2) -> {
            if (o1 == o2) {
                return 0;
            }
            int i = o1.priority() - o2.priority();
            if (i == 0) {
                return 1;
            } else {
                return i;
            }
        });
        this.executorInstanceMap = new ConcurrentHashMap<>();
    }

    public void scan() {

        File[] files = this.folder.listFiles();

        if (files == null) {
            this.logger.warning("无法扫描模块 插件目录为空");
            return;
        }

        for (File file : files) {

            if (file.isDirectory()) {
                continue;
            }

            Plugin plugin;

            try {
                plugin = new Plugin(file);
            } catch (ScanException exception) {
                this.logger.warning("扫描插件失败 " + file.getName(), exception);
                continue;
            }



        }

    }

}

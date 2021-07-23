package studio.blacktech.furryblackplus.core.schema.element;

import studio.blacktech.furryblackplus.core.utilties.LoggerX;

import java.io.File;

public class Plugin {

    private final LoggerX logger = new LoggerX(this.getClass());

    private final File file;

    public Plugin(File file) {


        this.file = file;


    }
}

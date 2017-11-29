package config;

import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;

@ShellComponent
public class Testing {
	
    @ShellMethod("Add two integers together.")
    public void add(int a, int b) {
        System.out.println( a + b);
    }
}

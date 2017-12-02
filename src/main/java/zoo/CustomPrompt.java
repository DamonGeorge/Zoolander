package zoo;


import org.jline.utils.AttributedString;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

@Component
public class CustomPrompt implements PromptProvider {

        @Override
        public AttributedString getPrompt() {
        	Session.terminalWidth = Session.terminal.getWidth() - 10;
        	if(Session.terminalWidth < 80)
        		System.out.println("Please increase your window size!");
        	return new AttributedString("zoo>");
        }

}

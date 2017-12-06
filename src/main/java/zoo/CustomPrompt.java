package zoo;

import org.jline.utils.AttributedString;
import org.springframework.shell.jline.PromptProvider;
import org.springframework.stereotype.Component;

/**
 * Override the Spring Shell Prompt with the custom 'zoo>' prompt.
 * Also this class continuously update the terminal width with each prompt
 * @author damongeorge
 *
 */
@Component
public class CustomPrompt implements PromptProvider {

        @Override
        public AttributedString getPrompt() {
        	//Get the current terminal width (minus a little padding)
        	Session.terminalWidth = Session.terminal.getWidth() - 5;
        	
        	if(Session.terminalWidth < 80) //Check for minimum width
        		System.out.println("Please increase your window size!");
        	
        	return new AttributedString("zoo>");
        }

}

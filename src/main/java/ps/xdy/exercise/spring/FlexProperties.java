package ps.xdy.exercise.spring;

import java.util.Properties;

import org.springframework.stereotype.Service;

@Service
public class FlexProperties extends Properties {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public FlexProperties(){
		put("name", "hahahah");
	}

}

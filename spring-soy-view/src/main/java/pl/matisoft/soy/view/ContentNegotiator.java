package pl.matisoft.soy.view;

import java.util.List;

/**
 * Implementations of this interface are responsible for determining whether or not they can support the requested
 * content types.
 *  
 * @author Abhijit Sarkar
 */
public interface ContentNegotiator {

	boolean isSupportedContentTypes(List<String> contentTypes);

	List<String> contentTypes();

}

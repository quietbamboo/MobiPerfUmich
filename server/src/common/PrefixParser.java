/**
 * @author Junxian Huang
 * @date Aug 29, 2009
 * @time 5:39:02 PM
 * @organization University of Michigan, Ann Arbor
 */
package common;

/**
 * @author Junxian Huang
 *
 */
public class PrefixParser {

	
	/**
	 * modified to support more rich prefix
	 * @param prefix
	 * @return null if prefix contains < 3 fields
	 */
	public String[] parsePrefix(String prefix){
		String[] prefix_array = prefix.trim().split("<");
		//<aaa><aaa><aaa>
		//"" "aaa>" "aaa>"  "aaa>"
		
		String[] result = new String[prefix_array.length - 1];
		for(int i = 0; i < prefix_array.length - 1; i++){
			result[i] = prefix_array[i + 1].split(">")[0];
		}
		
		if(result.length >= 3)
			return result;
		else
			return null;
	}

}

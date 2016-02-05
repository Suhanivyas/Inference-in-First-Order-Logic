//package inference;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.io.BufferedWriter;
import java.util.regex.Pattern;

class Sentence {
	ArrayList < String > left = new ArrayList < String > ();
	String right = new String();
	void print() {
		System.out.println("LeftPart");

		for (int i = 0; i < left.size(); i++)
		System.out.println(left.get(i));

		System.out.println("RightPart");
		System.out.println(right);

	}
}



public class inference {
	static BufferedWriter bw = null;
	static List < String > myList = new ArrayList < String > ();
	static HashMap < String, ArrayList < String >> Hashfacts = new HashMap < String, ArrayList < String >> ();
	static HashMap < String, ArrayList < Sentence >> rightInClause = new HashMap < String, ArrayList < Sentence >> ();
	static HashMap < String, ArrayList < String >> allRightS = new HashMap < String, ArrayList < String >> ();
	static ArrayList < String > visitedPredicates = new ArrayList < String > ();
	static Queue < String > queue = new LinkedList < String > ();

	//static Queue<String> query=new Queue<String> ();

	static ArrayList < String > constants = new ArrayList < String > ();
	static ArrayList < String > variableArr = new ArrayList < String > ();

	static int numQueries = 0;
	static int numClauses = 0;
	static ArrayList < String > queries = new ArrayList < String > ();
	//	static ArrayList<clauses> clauses=new ArrayList<clauses>();
	static ArrayList < String > kb = new ArrayList < String > ();



	//****************MAIN FUNCTION***************************//
	public static void main(String args[]) throws IOException {

		try {
			//String fileName = "/Users/suhanivyas/Documents/workspace/inference/src/inference/input_1-5.txt";
			String fileName=args[1];
			Scanner scanner = new Scanner(new File(fileName));
			File file = new File("output.txt");
			if (!file.exists()) {
				file.createNewFile();
			}
			FileWriter fw = new FileWriter(file);
			bw = new BufferedWriter(fw);

			while (scanner.hasNextLine()) {
				String line = scanner.nextLine();
				
				Scanner lineScanner = new Scanner(line);
				lineScanner.useDelimiter("\n");
				while (lineScanner.hasNext()) {
					String part = lineScanner.nextLine();
					part=part.trim();
					myList.add(part);
				}
				lineScanner.close();

			}
			scanner.close();
			//for(int i=0;i<myList.size();i++)
			//	System.out.println(myList.get(i));
			parseFile();
			//	printData();
			callQueries();
			/*for(int i=0;i<queries.size();i++){
				System.out.println("Query"+i+" "+queries.get(i));
			}
			HashMap < String, String > ipmap = new HashMap < String, String > ();
			ArrayList < HashMap < String, String >> retmap = backwardChaining("Sibling(John,Neelu)", ipmap);
			if (retmap == null) System.out.println("Final result false!!");
			else System.out.println("Final result true!!");*/

		} catch (Exception e) {
			e.printStackTrace();
		} finally {

			if (bw != null) bw.close();

		}
	}
    public static void callQueries()throws
	 IOException {
    	try{
		for(int i=0;i<queries.size();i++){
			visitedPredicates.clear();
			System.out.println(queries.get(i));
			HashMap<String,String> ipmap=new HashMap<String,String>();
			ArrayList<HashMap<String,String>> retmap=backwardChaining(queries.get(i),ipmap);
			if(retmap==null){
				String res1="FALSE";
				bw.write(res1);
		      bw.newLine();
			System.out.println("Final result false!!");
		}
			else{
				String res2="TRUE";
				bw.write(res2);
			    bw.newLine();
				System.out.println("Final result true!!");

			}
			
		}
		
	}
    	catch (IOException ioe) {
			   ioe.printStackTrace();
			}
    }

	public static void parseFile() {

		numQueries = Integer.valueOf(myList.get(0));
		int j;
		for (j = 1; j < numQueries + 1; j++) {
			String w=myList.get(j);
			w=w.trim();
			queries.add(w);
		}
		numClauses = Integer.valueOf(myList.get(j));
		j++;
		for (int i = j; i < myList.size(); i++) {
			if (myList.get(i).contains("=>")) {
				kb.add((myList.get(i)));
				makerightInClause(myList.get(i));
			} else {
				makefact(myList.get(i));
			}
		}
	}
	public static void makerightInClause(String s) {
		String[] vals = s.split("=>");

		String name = vals[1].substring(0, vals[1].indexOf("("));
		name = name.trim();
		//System.out.println("current key="+name);
		//add values to global arraylist  variableArr
		String[] temp_arg = s.substring(s.indexOf("(") + 1, s.indexOf(")")).split(",");
		ArrayList < String > temp_arr = new ArrayList < String > ();
		for (String st: temp_arg)
		temp_arr.add(st);
		for (String str: temp_arr)
		if (!variableArr.contains(str)) variableArr.add(str);

		//Adding right clause
		if (rightInClause.containsKey(name)) {
			ArrayList < Sentence > tempArray = rightInClause.get(name);

			Sentence tempSent = new Sentence();
			tempSent.right = vals[1];
			String[] tempA = vals[0].split("\\^");
			ArrayList < String > lef = new ArrayList < String > ();
			for (String sta: tempA)
			lef.add(sta);
			tempSent.left = lef;

			tempArray.add(tempSent);

			rightInClause.put(name, tempArray);



		} else {
			ArrayList < Sentence > tempArray = new ArrayList < Sentence > ();

			Sentence tempSent = new Sentence();
			tempSent.right = vals[1];
			String[] tempA = vals[0].split("\\^");
			ArrayList < String > lef = new ArrayList < String > ();
			for (String sta: tempA)
			lef.add(sta);
			tempSent.left = lef;
			tempArray.add(tempSent);
			rightInClause.put(name, tempArray);

		}

	}


	public static void makefact(String fact) {

		String[] temp_arg = fact.substring(fact.indexOf("(") + 1, fact.indexOf(")")).split(",");
		ArrayList < String > temp_arr = new ArrayList < String > ();
		for (String st: temp_arg)
		temp_arr.add(st);
		for (String str: temp_arr)
		if (!constants.contains(str)) constants.add(str);

		String key = fact.substring(0, fact.indexOf("("));
		if (Hashfacts.containsKey(key)) {
			ArrayList < String > tempArray = Hashfacts.get(key);
			tempArray.add(fact);
			Hashfacts.put(key, tempArray);

		} else {
			ArrayList < String > tempArray = new ArrayList < String > ();

			tempArray.add(fact);
			Hashfacts.put(key, tempArray);


		}
	}
	public static void printData() {
		/*	System.out.println("Queries");
		for (int j = 0; j < queries.size(); j++)
		System.out.println(queries.get(j));
		System.out.println("KB");

		for (int i = 0; i < kb.size(); i++) {
			System.out.println(kb.get(i));

		}*/
		System.out.println("Hashfacts");

		for (Map.Entry < String, ArrayList < String >> entry: Hashfacts.entrySet()) {
			System.out.println(entry.getKey() + ":");
			for (String s: entry.getValue())
			System.out.println(s);

		}
		/*System.out.println("Right ones");
		int j=1;
		for (Map.Entry < String,ArrayList < Sentence >> entry: rightInClause.entrySet()) {
			System.out.println("Entry num:"+j++);

			System.out.println(entry.getKey());
			int i=1;
			for (Sentence s: entry.getValue()){
				System.out.println(i++);

			s.print();
			}

		}*/
		/*System.out.println("constants");
		for(String h:constants)
			System.out.println(h);
		
		System.out.println("variablelist");
		for(String h:variableArr)
			System.out.println(h);*/



	}

	public static ArrayList < HashMap < String, String >> backwardChaining(String q, HashMap < String, String > map) {
		boolean emptyInputHashMap = false;
		if (map.isEmpty()) emptyInputHashMap = true;
		ArrayList < HashMap < String, String >> tempHashList = new ArrayList < HashMap < String, String >> ();

		ArrayList < String > currentQueryArgs = givePredArguements(q);
		boolean allVariables = true;
		for (int e = 0; e < currentQueryArgs.size(); e++) {
			if (!Character.isLowerCase(currentQueryArgs.get(e).charAt(0))) {
				allVariables = false;
				break;
			}
		}
		boolean noValueInHash = true;
		for (int e = 0; e < currentQueryArgs.size(); e++) {
			for (Map.Entry < String, String > entry: map.entrySet()) {
				if (map.containsKey(currentQueryArgs.get(e))) {
					noValueInHash = false;
					break;
				}
			}
			if (!noValueInHash) break;
		}


		//tempHashList.add(map);
		String currentPred = substitute(q, map);
		//System.out.println("Current Predicate=" + currentPred);
		//for (Map.Entry < String, String > entry: map.entrySet()) {
		//	System.out.println(entry.getKey() + ":" + entry.getValue());
		//}

		if (visitedPredicates.isEmpty()) {
			visitedPredicates.add(currentPred);
		} else {
			if (visitedPredicates.contains(currentPred)) return null;
			else {
				if (!map.isEmpty()) visitedPredicates.add(currentPred);
			}
		}



		boolean canGetAnyValue = false;
		if (emptyInputHashMap == true && allVariables == true || noValueInHash == true && allVariables == true) canGetAnyValue = true;
		q = q.trim();
		ArrayList < HashMap < String, String >> checkinFacts = existsInFacts(q, map, canGetAnyValue);

		if (checkinFacts != null) {
			//System.out.println("coming here ");
			return checkinFacts;
		} else {
			ArrayList < HashMap < String, String >> checkinPartialFacts = existsInPartialFact(q, map, emptyInputHashMap);
			if (checkinPartialFacts != null) {
				return checkinPartialFacts;
			}
			Queue < String > allMatchedRules = new LinkedList < String > ();
			for (int i = 0; i < kb.size(); i++) {
				if (matchedWithRule(getNextRule(kb.get(i)), q, map)) allMatchedRules.add(kb.get(i));
			}
			if (allMatchedRules.isEmpty()) {
				return null;
			} else {
				ArrayList < HashMap < String, String >> subsArr = new ArrayList < HashMap < String, String >> ();
				boolean gotContradiction = false;
				boolean ruleFalied = false;
				while (!allMatchedRules.isEmpty()) {
					String currentRule = allMatchedRules.remove();
					gotContradiction = false;
					String rightRule = currentRule.substring(currentRule.indexOf("=>") + 2, currentRule.length());
					HashMap < String, String > newMap = mergeMap(currentPred, rightRule);

					ArrayList < String > antecedents = getAntecedents(currentRule);
					int counter = 0;
					while (counter < antecedents.size() && gotContradiction != true) {
						//String currUsedPred=antecedents.get(counter);


						subsArr = backwardChaining(antecedents.get(counter), newMap);
						if (subsArr != null && counter == antecedents.size() - 1) {
							for (int y = 0; y < subsArr.size(); y++) {
								HashMap < String, String > cloned = (HashMap < String, String > ) map.clone();
								changeMapBack(subsArr.get(y), rightRule, cloned, q);
								tempHashList.add(cloned);
								subsArr = tempHashList;
							}
						}


						counter++;
						if (subsArr != null && gotContradiction != true) {
							//int totalsubs = subsArr.size();
							for (int j = 0; j < subsArr.size() && counter < antecedents.size(); j++) {
								gotContradiction = false;
								ArrayList < HashMap < String, String >> NewsubsArr = backwardChaining(antecedents.get(counter), subsArr.get(j));

								if (NewsubsArr != null) subsArr = NewsubsArr;

								if (NewsubsArr != null && counter == antecedents.size() - 1) {
									for (int y = 0; y < subsArr.size(); y++) {
										HashMap < String, String > cloned = (HashMap < String, String > ) map.clone();
										changeMapBack(subsArr.get(y), rightRule, cloned, q);
										tempHashList.add(cloned);
										subsArr = tempHashList;
									}
								}


								if (NewsubsArr != null) {
									counter++;
									if (j < subsArr.size()) newMap = NewsubsArr.get(j);
								}
								if (NewsubsArr != null && counter >= antecedents.size()) return tempHashList;

								if (NewsubsArr == null) gotContradiction = true;
								if (gotContradiction == true && j < subsArr.size()) continue;

							}

						} else {
							ruleFalied = true;
							break;
						}
					}
					if (gotContradiction) return null;
					if (ruleFalied) continue;
					if (!tempHashList.isEmpty()) break;


				}
				return subsArr;
			}
		}
	}




	public static HashMap < String, String > mergeMap(String currentPred, String rightPartRule) {
		HashMap < String, String > result = new HashMap < String, String > ();
		ArrayList < String > currentPredArg = givePredArguements(currentPred);
		ArrayList < String > rightPartRuleArg = givePredArguements(rightPartRule);
		for (int i = 0; i < rightPartRuleArg.size(); i++) {
			if (!rightPartRuleArg.get(i).equals(currentPredArg.get(i))) {
				if (!Character.isLowerCase(currentPredArg.get(i).charAt(0))) result.put(rightPartRuleArg.get(i), currentPredArg.get(i));
			}

		}

		return result;

	}
	public static void changeMapBack(HashMap < String, String > returnedMap, String rightPartRule, HashMap < String, String > cloned, String originalQ) {


		ArrayList < String > rightPartRuleArg = givePredArguements(rightPartRule);
		ArrayList < String > originalQArgs = givePredArguements(originalQ);
		for (int i = 0; i < originalQArgs.size(); i++) {
			if (Character.isLowerCase(originalQArgs.get(i).charAt(0))) {
				if (!cloned.containsKey(originalQArgs.get(i))) {
					if (returnedMap.containsKey(rightPartRuleArg.get(i))) {
						if (!Character.isLowerCase(returnedMap.get(rightPartRuleArg.get(i)).charAt(0))) {
							cloned.put(originalQArgs.get(i), returnedMap.get(rightPartRuleArg.get(i)));
						}
					} else {
						if(!Character.isLowerCase(rightPartRuleArg.get(i).charAt(0)))
						cloned.put(originalQArgs.get(i), rightPartRuleArg.get(i));
						
					}


				}
			}
		}



	}


	public static ArrayList < String > getAntecedents(String rule) {
		ArrayList < String > result = new ArrayList < String > ();
		String[] tempSpiltArr1 = rule.split(Pattern.quote("=>"));
		String[] tempSpiltArr2 = tempSpiltArr1[0].split(Pattern.quote("^"));
		for (String s: tempSpiltArr2)
		result.add(s);
		return result;

	}
	public static String getNextRule(String kb) {
		String[] splitRule = kb.split("=>");
		return splitRule[1];
	}
	public static boolean matchedWithRule(String ruleRight, String q, HashMap < String, String > map) {
		String[] rightPred = ruleRight.split(Pattern.quote("("));
		ArrayList < String > rightPredArg = givePredArguements(ruleRight);
		String[] qPred = q.split(Pattern.quote("("));
		ArrayList < String > qPredArg = givePredArguements(q);

		if (rightPred[0].trim().equals(qPred[0].trim()) && rightPredArg.size() == qPredArg.size()) {
			for (int i = 0; i < rightPredArg.size(); i++) {
				if (rightPredArg.get(i).equals(qPredArg.get(i))) {
					continue;
				} else if (map.containsKey(qPredArg.get(i))) {
					if (map.get(qPredArg.get(i)).equals(rightPredArg.get(i))) continue;
				} else {
					if (!Character.isLowerCase(qPredArg.get(i).charAt(0))) map.put(rightPredArg.get(i), qPredArg.get(i));
				}

			}
			return true;

		}
		return false;
	}





	public static ArrayList < HashMap < String, String >> existsInFacts(String q, HashMap < String, String > map, boolean canGetAnyValue) {
		if (canGetAnyValue) {
			String k = q.substring(0, q.indexOf("("));
			ArrayList < HashMap < String, String >> allHashMaps = new ArrayList < HashMap < String, String >> ();
		//	boolean notSame = false;
			ArrayList < String > qArgs = givePredArguements(q);
			HashMap < String, String > map1 = new HashMap < String, String > ();
			if (Hashfacts.containsKey(k)) {
				ArrayList < String > factList = Hashfacts.get(k);
				if (!factList.isEmpty()) {
					ArrayList < String > facArgs = givePredArguements(factList.get(0));
					if (qArgs.size() == facArgs.size()) {
						for (int w = 0; w < qArgs.size(); w++) {
							map1.put(qArgs.get(w), facArgs.get(w));
						}

					}
				}
				allHashMaps.add(map1);
			}

			if (!allHashMaps.isEmpty()) return allHashMaps;
			return null;


		} else {
			String k = q.substring(0, q.indexOf("("));
			ArrayList < HashMap < String, String >> allHashMaps = new ArrayList < HashMap < String, String >> ();
			boolean notSame = false;
			if (Hashfacts.containsKey(k)) {
				ArrayList < String > factList = Hashfacts.get(k);
				for (int i = 0; i < factList.size(); i++) {
					if (q.equals(factList.get(i))) {
						notSame = true;
						break;
					}
				}
				if (!allHashMaps.contains(map) && notSame == true) allHashMaps.add(map);
			}
			if (!allHashMaps.isEmpty()) return allHashMaps;
			return null;
		}
	}

	public static ArrayList < HashMap < String, String >> existsInPartialFact(String q, HashMap < String, String > map, boolean emptyInputHashMap) {


		String qPred = q.substring(0, q.indexOf("("));
		ArrayList < String > qArg = givePredArguements(q);
		ArrayList < HashMap < String, String >> allHashMaps = new ArrayList < HashMap < String, String >> ();
		if (Hashfacts.containsKey(qPred)) {
			ArrayList < String > factList = Hashfacts.get(qPred);
			for (int l = 0; l < factList.size(); l++) {
				HashMap < String, String > hashCopy = (HashMap < String, String > ) map.clone();
				boolean notSame = false;
				ArrayList < String > facArg = givePredArguements(factList.get(l));
				for (int i = 0; i < facArg.size(); i++) {
					if (qArg.get(i).equals(facArg.get(i))) {
						continue;
					} else if (hashCopy.containsKey(qArg.get(i))) {
						if (hashCopy.get(qArg.get(i)).equals(facArg.get(i))) {
							continue;
						} else {
							notSame = true;
							break;
						}
					} else if (!qArg.get(i).equals(facArg.get(i))) {

						if (Character.isLowerCase(qArg.get(i).charAt(0))) {
							if (!hashCopy.containsKey(qArg.get(i))) {
								hashCopy.put(qArg.get(i), facArg.get(i));
							}
						} else {
							notSame = true;
							break;
						}
					}
				}

				if (!allHashMaps.contains(hashCopy) && notSame == false) allHashMaps.add(hashCopy);
			}
		}
		if (!allHashMaps.isEmpty()) return allHashMaps;



		return null;
	}
	public static ArrayList < String > givePredArguements(String q) {
		ArrayList < String > result = new ArrayList < String > ();
		String[] tempSpiltArr1 = q.split(Pattern.quote("("));
		String[] tempSpiltArr2 = tempSpiltArr1[1].split(Pattern.quote(")"));
		String[] tempSpiltArr3 = tempSpiltArr2[0].split(",");
		for (String s: tempSpiltArr3)
		result.add(s);
		return result;

	}
	public static String substitute(String q, HashMap < String, String > map) {


		//Get all the arguments to make up a new query
		ArrayList < String > args = givePredArguements(q);
		//		String tokens[]=query.split(",");



		//		for(int i =0;i<args.length;i++){
		//			
		//			if(hm.containsKey(args[i].trim())){
		//				query = query.replaceAll(args[i].trim(),hm.get(args[i].trim()));
		//			}
		//			
		//		}
		int count = 0;
		for (int i = 0; i < args.size(); i++) {
			if (Character.isUpperCase(args.get(i).charAt(0))) {
				count++;
			}
		}

		if (count == args.size()) {
			return q;
		} else {
			String[] str = q.split(Pattern.quote("("));
			ArrayList < String > argsSubstituted = new ArrayList < String > ();

			for (int i = 0; i < args.size(); i++) {
				if (map.containsKey(args.get(i).trim())) {
					argsSubstituted.add(map.get(args.get(i).trim()));
				} else if (!map.containsKey(args.get(i).trim())) {
					argsSubstituted.add(args.get(i));

				}
			}
			StringBuffer sb = new StringBuffer();
			sb.append(str[0]);
			sb.append("(");
			for (int i = 0; i < argsSubstituted.size(); i++) {
				sb.append(argsSubstituted.get(i));

				if (i == argsSubstituted.size() - 1) {
					break;
				}
				sb.append(",");
			}
			sb.append(")");


			return sb.toString();
		}


	}



}

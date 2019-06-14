package com.mastermind.codebreaker;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class ProposalUtils {
	
	private static Random randomGenerator = new Random();

	//Create random consistent proposal for start
	public static int getFirstProposal(List<Integer> potInts, int d, int n) {

		int proposal = 0;

		List<Integer> potantialNumbersClone = new ArrayList<>(potInts);
		for(int i = 0 ; i<n; i++){

			int number = potantialNumbersClone.get(randomGenerator.nextInt(potantialNumbersClone.size()));

			while(i == 0 && number == 0){
				number = potantialNumbersClone.get(randomGenerator.nextInt(potantialNumbersClone.size()));
			}

			proposal = proposal + (((int) Math.pow(10, (n-i-1) )) * number);
			potantialNumbersClone.remove(Integer.valueOf(number));
		}
		return proposal;
	}

	//Replace function for mutations
	public static String replace(String proposal, int index, String digit) {
		if (index+1 == proposal.length()){
			return proposal.substring(0,index) + digit;
		}
		return proposal.substring(0,index) + digit + proposal.substring(index+1, proposal.length());
	}

	//Check proposal consistency, it has to get same score with proposal which is compared with own
	public static boolean isProposalConsistent(List<AbstractMap.SimpleEntry<Integer, Integer[]>> proposals, String newProposal, int N) {
		String iterProp;
		Integer[] newProposalScore = {0,0};
		Integer[] iterScore;

		//Comparing with all off old proposals
		for(int i = 0; i<proposals.size(); i++){
			newProposalScore[0] = 0; newProposalScore[1] = 0;
			iterProp = Integer.toString(proposals.get(i).getKey());
			iterScore = proposals.get(i).getValue();

			//Comparing digits
			for (int j = 0; j < N; j++) {
				if(iterProp.indexOf(newProposal.charAt(j)) >= 0){
					if (iterProp.charAt(j) == newProposal.charAt(j))
						newProposalScore[0]++;
					else
						newProposalScore[1]++;

				}
			}

			if (iterScore[0] != newProposalScore[0] && iterScore[1] != newProposalScore[1])
				return false;
		}
		return true;
	}

	//Heuristic compare. Firstly we evaluate  sum of "exact" and "misplace", secondly evaluate just "exact"
	public static boolean compareWithBestProposal(AbstractMap.SimpleEntry<Integer, Integer[]> bestProposal, int proposal, int exact, int misplaced) {
		if (exact + misplaced > bestProposal.getValue()[0] + bestProposal.getValue()[0]){
			return true;
		}else if (exact + misplaced < bestProposal.getValue()[0] + bestProposal.getValue()[0]){
			return false;
		}else {
			return exact > bestProposal.getValue()[0];
		}
	}

	//We produce new proposals according to bestProposal
	public static int getNewProposal(List<AbstractMap.SimpleEntry<Integer, Integer[]>> proposals, List<Integer> potInts, int N, 
			AbstractMap.SimpleEntry<Integer, Integer[]> bestProposal) {
		String proposal;
		String newProposal;
		int exact = bestProposal.getValue()[0];
		int misplace = bestProposal.getValue()[1];
		List<Integer> numberList;

		do {	//Checking consistency of new proposal
			numberList = new ArrayList<>(potInts);
			proposal = Integer.toString(bestProposal.getKey());
			newProposal = "x";

			for(int i = 0; i<N-1; i++){
				newProposal = newProposal + "x";
			}

			ArrayList<Integer> newProposalIndices;
			ArrayList<Integer> originProposalIndices;
			int commonIndicesNumber;
			do {	//Remaining indices after placing misplaced values should greater than number of exact values. We control this

				//Create Index lists
				newProposalIndices = new ArrayList<Integer>();
				for(int i = 0 ; i<N; i++){
					newProposalIndices.add(i);
				}
				originProposalIndices = new ArrayList<>(newProposalIndices);


				int index1;
				int index2;
				for(int i = 0; i<misplace; i++){ //Placing misplaced values to another random index
					do{
						index1 = originProposalIndices.get(randomGenerator.nextInt(originProposalIndices.size()));
						index2 = newProposalIndices.get(randomGenerator.nextInt(newProposalIndices.size()));
					}while ((index2 == 0 && proposal.charAt(index1) == '0') ||	// Value of first index can't be 0
							index1 == index2);

					newProposal = ProposalUtils.replace(newProposal, index2, String.valueOf(proposal.charAt(index1)));
					numberList.remove(Integer.valueOf(Character.getNumericValue(proposal.charAt(index1))));
					originProposalIndices.remove(Integer.valueOf(index1));
					newProposalIndices.remove(Integer.valueOf(index2));
				}

				commonIndicesNumber = 0;
				for (int index: originProposalIndices) {	//Calculate number of remaining index
					if (newProposalIndices.contains(index))
						commonIndicesNumber++;
				}
			}while (commonIndicesNumber < exact);



			int index;
			for(int i = 0; i<exact; i++){	//Random selection of exact values, we don't move their places
				do {
					index = newProposalIndices.get(randomGenerator.nextInt(newProposalIndices.size()));
				}while (!originProposalIndices.contains(index));

				newProposal = ProposalUtils.replace(newProposal, index, String.valueOf(proposal.charAt(index)));
				newProposalIndices.remove(Integer.valueOf(index));
				originProposalIndices.remove(Integer.valueOf(index));
				numberList.remove(Integer.valueOf(Character.getNumericValue(newProposal.charAt(index))));

			}

			for(int i = 0; i<originProposalIndices.size(); i++){	//We remove the remaining numbers from the bestProposal from the list of potential numbers.
				numberList.remove(Integer.valueOf(Character.getNumericValue(proposal.charAt(originProposalIndices.get(i)))));
			}

			int number;
			for(int i = 0; i<newProposalIndices.size(); i++){	//We place random and appropriate numbers from remaining numbers to remaining indices
				do {
					number = numberList.get(randomGenerator.nextInt(numberList.size()));
				}while (number == 0 && newProposalIndices.get(i) == 0);

				newProposal = ProposalUtils.replace(newProposal, newProposalIndices.get(i), String.valueOf(number));
				numberList.remove(Integer.valueOf(number));
			}


		}while(!ProposalUtils.isProposalConsistent(proposals, newProposal, N));

		return Integer.parseInt(newProposal);

	}

}

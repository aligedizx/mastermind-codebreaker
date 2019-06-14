package com.mastermind.codebreaker;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.time.StopWatch;
import org.springframework.stereotype.Component;

@Component("breaking.command")
public class BreakingCommand {

	StopWatch stopwatch;
	Scanner sc = new Scanner(System.in);
	long maxTime;
	
	public void startBreaking(int D, int N, int maxTurn, long maxTime) {
		stopwatch = StopWatch.createStarted();
		this.maxTime = maxTime;
		List<AbstractMap.SimpleEntry<Integer, Integer[]>> proposals = new ArrayList<>();
		List<Integer> potInts = getPotantialNumbers(D);
		int proposal = ProposalUtils.getFirstProposal(potInts, D, N);
		int round  = 0;
		System.out.println(proposal);


		String line = getResponse();


		String argArr[] = line.split(" ");
		int exact = Integer.parseInt(argArr[0]);
		int misplaced = Integer.parseInt(argArr[1]);

		AbstractMap.SimpleEntry<Integer, Integer[]> bestProposal = new AbstractMap.SimpleEntry<>(proposal, new Integer[]{exact, misplaced});
		proposals.add(bestProposal);

		do{
			if(++round >= maxTurn) // if turn is over, exit the program
				break;

			if (exact == 0 && misplaced == 0){	//if it does not contain any digits
				updatePotantialNumbers(potInts, proposal, N);
				proposal = ProposalUtils.getFirstProposal(potInts, D, N); //get new proposal


			}else{  // Check and Update best proposal
				if (ProposalUtils.compareWithBestProposal(bestProposal, proposal, exact, misplaced)){
					bestProposal = new AbstractMap.SimpleEntry<>(proposal, new Integer[]{exact, misplaced});
				}
				proposal = ProposalUtils.getNewProposal(proposals, potInts, N, bestProposal); // Create new consistent proposal
			}

			System.out.println(proposal);
			line = getResponse();
			argArr = line.split(" ");
			exact = Integer.parseInt(argArr[0]);
			misplaced = Integer.parseInt(argArr[1]);
			proposals.add(new AbstractMap.SimpleEntry<>(proposal, new Integer[]{exact, misplaced})); // Add proposal and its score to history

			if (exact == N){ //If proposal is correct exit the program
				break;
			}

		}while(true);
		
		sc.close();
	}

	private String getResponse() {
		// stop timer before getting new values
		stopwatch.suspend();
		// calculate remining time
		maxTime -= stopwatch.getTime(TimeUnit.MILLISECONDS);
		if(maxTime < 0) {
			System.exit(0); //If time is out, exit the program
		}

		String line =  sc.nextLine();
		// restart timer after getting new values

		stopwatch.resume();

		return line;
	}


	private List<Integer> getPotantialNumbers(int D) {
		List<Integer> returnList = new ArrayList<>();
		for(int i = 0 ; i<D ; i++) {
			returnList.add(i);
		}
		return returnList;
	}

	private void updatePotantialNumbers(List<Integer> potInts, int proposal, int n) {
		for(int i = 0; i<n ; i++) {
			int misNumber = proposal % 10;
			potInts.removeAll(Collections.singleton(misNumber));
			proposal = (proposal - misNumber) / 10;
		}
	}
}

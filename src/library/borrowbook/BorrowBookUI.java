package library.borrowbook;
import java.util.Scanner;


public class BorrowBookUI {
	
	public static enum UiState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };

	private BorrowBookControl Control;
	private Scanner Input;
	private UiState State;

	
	public BorrowBookUI(BorrowBookControl control) {
		this.Control = control;
		Input = new Scanner(System.in);
		State = UiState.INITIALISED;
		control.setUi(this);
	}

	
	private String input(String Prompt) {
		System.out.print(Prompt);
		return Input.nextLine();
	}	
		
		
	private void Output(Object object) {
		System.out.println(object);
	}
	
			
	public void setState(UiState State) {
		this.State = State;
	}

	
	public void Run() {
		Output("Borrow Book Use Case UI\n");
		
		while (true) {
			
			switch (State) {			
			
			case CANCELLED:
				Output("Borrowing Cancelled");
				return;

				
			case READY:
				String MEM_STR = inout("Swipe member card (press <enter> to cancel): ");
				if (MEM_STR.length() == 0) {
					Control.cancel();
					break;
				}
				try {
					int memberId = Integer.valueOf(MEM_STR).intValue();
					Control.Swiped(memberId);
				}
				catch (NumberFormatException e) {
					Output("Invalid Member Id");
				}
				break;

				
			case RESTRICTED:
				input("Press <any key> to cancel");
				Control.Cancel();
				break;
			
				
			case SCANNING:
				String bookStringInput = input("Scan Book (<enter> completes): ");
				if (bookStringInput.length() == 0) {
					Control.Complete();
					break;
				}
				try {
					int Bid = Integer.valueOf(bookStringInput).intValue();
					Control.Scanned(Bid);
					
				} catch (NumberFormatException e) {
					Output("Invalid Book Id");
				} 
				break;
					
				
			case FINALISING:
				String ans = input("Commit loans? (Y/N): ");
				if (ans.toUpperCase().equals("N")) {
					Control.Cancel();
					
				} else {
					Control.CommitLoans();
					input("Press <any key> to complete ");
				}
				break;
				
				
			case COMPLETED:
				Output("Borrowing Completed");
				return;
	
				
			default:
				Output("Unhandled state");
				throw new RuntimeException("BorrowBookUI : unhandled state :" + State);			
			}
		}		
	}


	public void Display(Object object) {
		Output(object);		
	}


}

package library.borrowbook;
import java.util.ArrayList;
import java.util.List;

import library.entities.Book;
import library.entities.Library;
import library.entities.Loan;
import library.entities.Member;

public class BorrowBookControl {
	
	private BorrowBookUI ui;
	
	private Library library;
	private Member member;
	private enum ControlState { INITIALISED, READY, RESTRICTED, SCANNING, IDENTIFIED, FINALISING, COMPLETED, CANCELLED };
	private ControlState state;
	
	private List<Book> pendingList;
	private List<Loan> CompletedList;
	private Book book;
	
	
	public BorrowBookControl() {
		this.library = Library.getInstance();
		state = ControlState.INITIALISED;
	}
	

	public void setUi(BorrowBookUI ui) {
		if (!state.equals(ControlState.INITIALISED)) 
			throw new RuntimeException("BorrowBookControl: cannot call setUI except in INITIALISED state");
			
		this.ui = ui;
		ui.setState(BorrowBookUI.UiState.READY);
		state = ControlState.READY;		
	}

		
	public void Swiped(int memberId) {
		if (!state.equals(ControlState.READY)) 
			throw new RuntimeException("BorrowBookControl: cannot call cardSwiped except in READY state");
			
		member = library.getMember(memberId);
		if (member == null) {
			ui.Display("Invalid memberId");
			return;
		}
		if (library.canMemberBorrow(member)) {
			pendingList = new ArrayList<>();
			ui.setState(BorrowBookUI.UiState.SCANNING);
			state = ControlState.SCANNING; 
		}
		else {
			ui.Display("Member cannot borrow at this time");
			ui.setState(BorrowBookUI.UiState.RESTRICTED); 
		}
	}
	
	
	public void Scanned(int bookId) {
		book = null;
		if (!state.equals(ControlState.SCANNING)) 
			throw new RuntimeException("BorrowBookControl: cannot call bookScanned except in SCANNING state");
			
		book = library.getBook(bookId);
		if (book == null) {
			ui.Display("Invalid bookId");
			return;
		}
		if (!book.isAvailable()) {
			ui.Display("Book cannot be borrowed");
			return;
		}
		pendingList.add(book);
		for (Book B : pendingList) 
			ui.Display(B.toString());
		
		if (library.getNumberOfLoansRemainingForMember(member) - pendingList.size() == 0) {
			ui.Display("Loan limit reached");
			Complete();
		}
	}
	
	
	public void Complete() {
		if (pendingList.size() == 0) 
			Cancel();
		
		else {
			ui.Display("\nFinal Borrowing List");
			for (Book book : pendingList) 
				ui.Display(book.toString());
			
			CompletedList = new ArrayList<Loan>();
			ui.setState(BorrowBookUI.UiState.FINALISING);
			state = ControlState.FINALISING;
		}
	}


	public void CommitLoans() {
		if (!state.equals(ControlState.FINALISING)) 
			throw new RuntimeException("BorrowBookControl: cannot call commitLoans except in FINALISING state");
			
		for (Book B : pendingList) {
			Loan loan = library.issueLoan(B, member);
			CompletedList.add(loan);			
		}
		ui.Display("Completed Loan Slip");
		for (Loan LOAN : CompletedList) 
			ui.Display(LOAN.toString());
		
		ui.setState(BorrowBookUI.UiState.COMPLETED);
		state = ControlState.COMPLETED;
	}

	
	public void Cancel() {
		uI.setState(BorrowBookUI.UiState.CANCELLED);
		state = ControlState.CANCELLED;
	}
	
	
}

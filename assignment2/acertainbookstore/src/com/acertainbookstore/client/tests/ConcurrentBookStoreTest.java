package com.acertainbookstore.client.tests;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.acertainbookstore.business.Book;
import com.acertainbookstore.business.BookCopy;
import com.acertainbookstore.business.BookRating;
import com.acertainbookstore.business.CertainBookStore;
import com.acertainbookstore.business.ConcurrentCertainBookStore;
import com.acertainbookstore.business.ImmutableStockBook;
import com.acertainbookstore.business.StockBook;
import com.acertainbookstore.client.BookStoreHTTPProxy;
import com.acertainbookstore.client.StockManagerHTTPProxy;
import com.acertainbookstore.interfaces.BookStore;
import com.acertainbookstore.interfaces.StockManager;
import com.acertainbookstore.utils.BookStoreConstants;
import com.acertainbookstore.utils.BookStoreException;

public class ConcurrentBookStoreTest {
	
	private static final int TEST_ISBN = 3044560;
	private static final int NUM_COPIES = 5;
	private static boolean localTest = true;
	private static StockManager storeManager;
	private static BookStore client;

	@BeforeClass
	public static void setUpBeforeClass() {
		try {
			String localTestProperty = System
					.getProperty(BookStoreConstants.PROPERTY_KEY_LOCAL_TEST);
			localTest = (localTestProperty != null) ? Boolean
					.parseBoolean(localTestProperty) : localTest;
			if (localTest) {
				ConcurrentCertainBookStore store = new ConcurrentCertainBookStore();
				storeManager = store;
				client = store;
			} else {
				storeManager = new StockManagerHTTPProxy(
						"http://localhost:8081/stock");
				client = new BookStoreHTTPProxy("http://localhost:8081");
			}
			storeManager.removeAllBooks();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	
	@Test
	public void testBuyAddCopies()
	{
		Set<StockBook> bookSet = new HashSet<StockBook>();
		int nrBuy = 100;
		int nrAdd = 500;
		int testISBN = 10;
		
		bookSet.add(new ImmutableStockBook(testISBN,
				"Book",
				"Author", (float) 99, 1, 0, 0, 0, false));
		try {
			storeManager.addBooks(bookSet);
		} catch (BookStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//add copies
		Set<BookCopy> booksToAdd = new HashSet<BookCopy>();
		BookCopy bookToAdd = new BookCopy(testISBN, nrAdd);
		booksToAdd.add(bookToAdd);
		
		//buy copies
		Set<BookCopy> booksToBuy = new HashSet<BookCopy>();
		BookCopy bookToBuy = new BookCopy(testISBN, nrBuy);
		booksToBuy.add(bookToBuy);
		
		// Create clients
		Thread c1 = new Thread (new ConcurrentAddCopiesThread(booksToAdd));
		Thread c2 = new Thread (new ConcurrentBuyBooksThread(booksToBuy));

		// Run them
		c1.start();
		c2.start();
		
	
				try {
					c1.join();
					c2.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
				List<StockBook> after = null;
				try {
					after = storeManager.getBooks();
				} catch (BookStoreException e) {
					e.printStackTrace();
					fail();
				}
				// Check stock at the end
				for (StockBook book : after) {
				
					if (testISBN == book.getISBN()) {
						assertTrue(after.size() == 1); 
					}
				}

	}
	
	
	
	@Test
	public void testMultipleBuyAddCopies()
	{
	
		Set<StockBook> bookSet = new HashSet<StockBook>();

		Integer testISBN1 = 101;
		Integer testISBN2 = 102;
		Integer testISBN3 = 103;
		
		// Books to test on
		bookSet.add(new ImmutableStockBook(testISBN1,
				"Book1",
				"A1", (float) 99, 5, 0, 0, 0, false));
		bookSet.add(new ImmutableStockBook(testISBN2,
				"Book2",
				"A2", (float) 98, 5, 0, 0, 0, false));	
		bookSet.add(new ImmutableStockBook(testISBN3,
				"Book3",
				"A3", (float) 97, 5, 0, 0, 0, false));
		//add the books to the database,
		// and initial stock
		Set<Integer> isbns = new HashSet<Integer>();
		isbns.add(testISBN1);
		isbns.add(testISBN2);
		isbns.add(testISBN3);
		BookCopy bookCopy1 = new BookCopy(testISBN1, 2);
		BookCopy bookCopy2 = new BookCopy(testISBN2, 2);
		BookCopy bookCopy3 = new BookCopy(testISBN3, 2);
		Set<BookCopy> bookCopySet = new HashSet<BookCopy>();
		bookCopySet.add(bookCopy1);
		bookCopySet.add(bookCopy2);
		bookCopySet.add(bookCopy3);
		
		List<StockBook> before = null;
		List<StockBook> after = null;
		try {
			before = storeManager.getBooks();
			after = storeManager.getBooks();
			storeManager.addBooks(bookSet);
		} catch (BookStoreException e1) {
			e1.printStackTrace();
			fail();
		}
		assertTrue(after.addAll(bookSet));
		
		// Create clients
		Thread getter = new Thread (new ConcurrentGetBooksThread(before, after, isbns));
		Thread setter = new Thread (new ConcurrentBuyAndAddThread(bookCopySet));
		
		// Run them
		getter.start();
		setter.start();
	
		// Wait
		try {
			getter.join();
			setter.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
	}
	
	
	@Test
	public void testConcurrentRating () {
		Set<StockBook> bookSet = new HashSet<StockBook>();

		Integer testISBN1 = 104;
		Integer testISBN2 = 105;
		
		// Books to test on
		bookSet.add(new ImmutableStockBook(testISBN1,
				"Book11",
				"A11", (float) 99, 5, 0, 0, 0, false));
		bookSet.add(new ImmutableStockBook(testISBN2,
				"Book12", 
				"A12", (float) 98, 5, 0, 0, 0, false));	
		
		// First we need to add the books to the database
		try {
			storeManager.addBooks(bookSet);
		} catch (BookStoreException e1) {
			e1.printStackTrace();
			fail();
		}

		BookRating rating1 = new BookRating(testISBN1, 4);
		BookRating rating2 = new BookRating(testISBN2, 5);
		Set<BookRating> ratings = new HashSet<BookRating>();
		ratings.add(rating1);
		ratings.add(rating2);
		
		// Create clients
		Thread rater1 = new Thread (new ConcurrentRatingThread(ratings, 2));
		Thread rater2 = new Thread (new ConcurrentRatingThread(ratings, 2));
		
		// Run threads
		rater1.start();
		rater2.start();
	
		// Wait
		try {
			rater1.join();
			rater2.join();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	
		List<StockBook> books = null;
		try {
			books = storeManager.getBooks();
		} catch (BookStoreException e) {
			e.printStackTrace();
			fail();
		}
	
		// Total expected rating:
		int expectedRatingISBN1 = 4 * (2 + 2);
		int expectedRatingISBN2 = 5 * (2 + 2);
		int expectedAvgRatingISBN1 = expectedRatingISBN1 / 4;
		int expectedAvgRatingISBN2 = expectedRatingISBN2 / 4;
		
		for (StockBook book : books) {
			if (book.getISBN() == testISBN1) {
				assertTrue(book.getAverageRating() == expectedAvgRatingISBN1);
			}
			if (book.getISBN() == testISBN2) {
				assertTrue(book.getAverageRating() == expectedAvgRatingISBN2);
			}
		}
	}
	
	
	@Test
	public void testBuyCopiesMultiple()
	{
		Set<StockBook> bookSet = new HashSet<StockBook>();
		int nrBuy1 = 200;
		int nrBuy2 = 200;
		int testISBN = 200;
		
		bookSet.add(new ImmutableStockBook(testISBN,
				"Book",
				"Author", (float) 99, 1, 0, 0, 0, false));
		try {
			storeManager.addBooks(bookSet);
		} catch (BookStoreException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//add copies
		Set<BookCopy> booksToBuy1 = new HashSet<BookCopy>();
		BookCopy bookToBuy1 = new BookCopy(testISBN, nrBuy1);
		booksToBuy1.add(bookToBuy1);
		
		//buy copies
		Set<BookCopy> booksToBuy2 = new HashSet<BookCopy>();
		BookCopy bookToBuy2 = new BookCopy(testISBN, nrBuy2);
		booksToBuy2.add(bookToBuy2);
		
		// Create clients
		Thread c1 = new Thread (new ConcurrentAddCopiesThread(booksToBuy1));
		Thread c2 = new Thread (new ConcurrentBuyBooksThread(booksToBuy2));

		// Run them
		c1.start();
		c2.start();
		
		// Wait
				try {
					c1.join();
					c2.join();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			
				List<StockBook> after = null;
				try {
					after = storeManager.getBooks();
				} catch (BookStoreException e) {
					e.printStackTrace();
					fail();
				}
				// Recount
				for (StockBook book : after) {
					// Initial stock was 5
					if (testISBN == book.getISBN()) {
						assertTrue(after.size() == 1); 
					}
				}

	}
	
	
	

	
	
	
	//Runnable class for adding copies
	public static class ConcurrentAddCopiesThread implements Runnable {
		Set<BookCopy> bookCopies;
		public ConcurrentAddCopiesThread (Set<BookCopy> bookCopies) {
			this.bookCopies = bookCopies;
		}
		public void run() {
			try {
				storeManager.addCopies(bookCopies);
			} catch (BookStoreException e) {
				e.printStackTrace();
			fail();
			}
		}
	}
	
	
	//Runnable class for buying copies
	public static class ConcurrentBuyBooksThread implements Runnable {
		Set<BookCopy> books;
		public ConcurrentBuyBooksThread (Set<BookCopy> books) {
			this.books = books;
		}
		public void run() {
			try {
				client.buyBooks(books);
			} catch (BookStoreException e) {
				e.printStackTrace();
				fail();
			}
		}
	}
	
	
	
	public static class ConcurrentGetBooksThread implements Runnable {
		Set<Integer> isbns;
	
		List<StockBook> before;
		List<StockBook> after;
		public ConcurrentGetBooksThread (List<StockBook> before,
									   List<StockBook> after,
									   Set<Integer> ISBNList) {
			this.before = before;
			this.after  = after;
			this.isbns  = ISBNList;
			
		}

		public void run() {
			List<StockBook> booksFromServer = null;
				try {
					booksFromServer = storeManager.getBooks();
				} catch (BookStoreException e) {
					e.printStackTrace();
		
				}
				if (! (compareBooks(booksFromServer, before) 
				    || compareBooks(booksFromServer, after))) {
					// Make error for JUnit
				
				}
		}
		public boolean compareBooks (List<StockBook> books1, List<StockBook> books2) {
			class StockBookCompareISBN implements Comparator<StockBook> {
				@Override
				public int compare(StockBook book0, StockBook book1) {
					return (int) (book1.getISBN() - book0.getISBN());
				}
			}
			Collections.sort(books1, new StockBookCompareISBN());
			Collections.sort(books2, new StockBookCompareISBN());
			if (books1.size() != books2.size()) {
				return false;
			}
			for (int i = 0; i < books1.size(); i++) {
				if (! books1.get(i).equals(books2.get(i))) {
					return false;
				}
			}
			return true;
		}
	}
	public static class ConcurrentBuyAndAddThread implements Runnable {
		Set<BookCopy> bookCopies;
		
		public ConcurrentBuyAndAddThread (Set<BookCopy> bookCopies) {
			this.bookCopies = bookCopies;
		
		}
		public void run() {
			try {
					client.buyBooks(bookCopies);
					storeManager.addCopies(bookCopies);
				} catch (BookStoreException e) {
					e.printStackTrace();
				
				}
		}
	}
	
	
	public static class ConcurrentRatingThread implements Runnable {
		Set<BookRating> ratings;
		int numRatings;
		public ConcurrentRatingThread (Set<BookRating> ratings, int numRatings) {
			this.ratings = ratings;
			this.numRatings = numRatings;
		}
		public void run() {
			for (int i = 0; i < numRatings; i++) {
				try {
					client.rateBooks(ratings);
				} catch (BookStoreException e) {
					e.printStackTrace();
			
				}
			}
		}
	}
	
	
	
	
	
	
	

	@AfterClass
	public static void tearDownAfterClass() throws BookStoreException {
		storeManager.removeAllBooks();
		if (!localTest) {
			((BookStoreHTTPProxy) client).stop();
			((StockManagerHTTPProxy) storeManager).stop();
		}
	}


}

// DoubleHashing Hash table class
//
// CONSTRUCTION: an approximate initial size or default of 101
//
// ******************PUBLIC OPERATIONS*********************
// bool insert( x )       --> Insert x
// bool remove( x )       --> Remove x
// bool contains( x )     --> Return true if x is present
// void makeEmpty( )      --> Remove all items


/**********************************************************
 * Probing table implementation of hash tables.
 * Note that all "matching" is based on the equals method.
 * @author Mark Allen Weiss
 *      with DoubleHashing modification by Dallin Drollinger
 **********************************************************/
public class DoubleHashingHashTable<AnyType> {

    //Private variables that make our HashTable
    private static final int DEFAULT_TABLE_SIZE = 101;
    private HashEntry<AnyType>[] array;   // The array of elements
    private int occupiedCt;                 // The number of occupied cells
    private int theSize;                    // Current size
    private int numberOfFinds;              // Times Find is called
    private int numberOfProbs;              // Times that we probe values

    /******************************************************
     * Hash table constructors setting up table size
     * Smallest allowed size is 3
     *******************************************************/
    public DoubleHashingHashTable() {
        this(DEFAULT_TABLE_SIZE);
    }

    public DoubleHashingHashTable(int size) {
	    if (size < 3) size = 3;
	    numberOfFinds = 0;
	    numberOfProbs = 0;
        allocateArray( size );
        doClear( );
    }

    /******************************************************
     * This internal private class makes up each item in
     * our HashTable and is used to show when a slot is full
     *******************************************************/
    private static class HashEntry<AnyType> {
        public AnyType  element;   // the element
        public boolean isActive;  // false if marked deleted

        public HashEntry(AnyType e) {
            this(e, true);
        }

        public HashEntry(AnyType e, boolean i) {
            element  = e;
            isActive = i;
        }
    }

    /*******************************************************
     * Insert into the hash table. If the item is
     * already present, do nothing.
     * @param x the item to insert.
     *******************************************************/
    public boolean insert(AnyType x) {
        // Insert x as active
        int currentPos = findPos(x);
        if(isActive(currentPos))
            return false;

        array[currentPos] = new HashEntry<>(x, true);
        theSize++;

        // Rehash; see Section 5.5
        if(++occupiedCt > array.length / 2)
            rehash();

        return true;
    }

    /***********************************************************
     * Find an item in the hash table and return it
     * @param x the item to search for.
     * @return the matching item.
     **********************************************************/
    public AnyType find(AnyType x) {
        numberOfFinds++;
        int currentPos = findPos(x);
        if (!isActive(currentPos)) {
            return null;
        }
        else {
            return array[currentPos].element;
        }
    }

    /***********************************************************
     * Find an item in the hash table and return true if found
     * @param x the item to search for.
     * @return true if item is found
     **********************************************************/
    public boolean contains(AnyType x) {
        int currentPos = findPos(x);
        return isActive(currentPos);
    }

    /**********************************************************
     * Method that returns the position of the given item
     * @param x the item to search for.
     * @return the position where the search terminates.
     **********************************************************/
    private int findPos(AnyType x) {
        int currentPos = myhash(x);
        HashEntry entry = array[currentPos];
        numberOfProbs++;

        if (entry != null && !entry.element.equals(x)) {
            int hashStep = myhash2(x);
            int newPos;
            int i = 0;

            do {
                newPos = (currentPos + (i++ * hashStep)) % array.length;
                if (newPos < 0) newPos += array.length;
                entry = array[newPos];
                numberOfProbs++;
            } while (entry != null && !entry.element.equals(x));
            return newPos;
        }
        return currentPos;
    }

    /**********************************************************
     * Remove from the hash table.
     * @param x the item to remove.
     * @return true if item removed
     **********************************************************/
    public boolean remove(AnyType x) {
        int currentPos = findPos(x);
        if(isActive(currentPos)) {
            array[currentPos].isActive = false;
            theSize--;
            return true;
        }
        else
            return false;
    }

    /***********************************************************
     * Make the hash table logically empty.
     **********************************************************/
    public void makeEmpty() {
        doClear();
    }

    /*********************************************************
     * Expand the hash table through rehashing.
     *********************************************************/
    private void rehash() {
        HashEntry<AnyType>[] oldArray = array;
		
        // Create a new double-sized, empty table
        allocateArray(2 * oldArray.length);
        occupiedCt = 0;
        theSize = 0;

        // Copy table over
        for(HashEntry<AnyType> entry : oldArray)
            if(entry != null && entry.isActive)
                insert(entry.element);
    }

    /***********************************************************
     * Get current size.
     * @return the size.
     **********************************************************/
    public int size() {
        return theSize;
    }

    /***********************************************************
     * Get length of internal table.
     * @return the size.
     **********************************************************/
    public int capacity() {
        return array.length;
    }

    /*******************************************************
     * The toString method override
     * prints out certain HashTable information depending on
     * specified type of info needed
     * @param type, limit <- type is what is wanted, limit is for
     *       printing out certain # of entries when type="entries"
     *******************************************************/
    public String toString (String type){
        switch (type.toLowerCase()) {
            case "probs": return numberOfProbs+"\n";
            case "finds": return numberOfFinds+"\n";
            case "items": return size()+"\n";
            case "length": return capacity()+"\n";
            default: return this.toString();
        }
    }

    public String toString (String type, int limit) {
        if (type.toLowerCase().equals("entries")) return this.toString(limit);
        else return this.toString();
    }

    private String toString (int limit){
        StringBuilder sb = new StringBuilder();
        int ct=0;
        String space; //Used for custom spacing
        for (int i=0; i < array.length && ct < limit; i++){
            if (array[i]!=null && array[i].isActive) {
                space = new String(new char[(int)Math.log10((double)array.length) - (int)Math.log10((double)i == 0 ? 1 : i)]).replace("\0", " ");
                sb.append(i + space + ":  " + array[i].element + "\n");
                ct++;
            }
        }
        return sb.toString();
    }


    /***********************************************************
     * Return true if currentPos exists and is active.
     * @param currentPos the result of a call to findPos.
     * @return true if currentPos is active.
     **********************************************************/
    private boolean isActive(int currentPos) {
        return array[currentPos] != null && array[currentPos].isActive;
    }

    private void doClear() {
        occupiedCt = 0;
        for(int i = 0; i < array.length; i++)
            array[i] = null;
    }

    /******************************************************
     * hash function to calculate 1st attempt at inserting
     *******************************************************/
    private int myhash(AnyType x) {
        int hashVal = x.hashCode();

        hashVal %= array.length;
        if(hashVal < 0)
            hashVal += array.length;        

        return hashVal;
    }

    /******************************************************
     * hash function to calculate step value for attempts at
     * inserting after collision
     *******************************************************/
    private int myhash2(AnyType x) {
        int hashVal = (x.hashCode() % (array.length - 2));
        if (hashVal < 0) hashVal += (array.length - 2);
	    return 1 + hashVal;
    }

    /***********************************************************
     * Internal method to allocate array.
     * @param arraySize the size of the array.
     **********************************************************/
    private void allocateArray(int arraySize) {
        array = new HashEntry[nextPrime(arraySize)];
    }

    /***********************************************************
     * Internal method to find a prime number at least as large as n.
     * @param n the starting number (must be positive).
     * @return a prime number larger than or equal to n.
     **********************************************************/
    private static int nextPrime(int n) {
        if(n % 2 == 0)
            n++;

        for(; !isPrime(n); n += 2)
            ;

        return n;
    }

    /***********************************************************
     * Internal method to test if a number is prime.
     * Not an efficient algorithm.
     * @param n the number to test.                             
     * @return the result of the test.
     **********************************************************/
    private static boolean isPrime(int n) {
        if(n == 2 || n == 3)
            return true;

        if(n == 1 || n % 2 == 0)
            return false;

        for(int i = 3; i * i <= n; i += 2)
            if(n % i == 0)
                return false;

        return true;
    }

    // Simple main
    public static void main(String[] args) {
        DoubleHashingHashTable<String> H = new DoubleHashingHashTable<>();
        long startTime = System.currentTimeMillis();

        final int NUMS = 2000000;
        final int GAP  = 37;

        System.out.println("Checking... (no more output means success)");

        for(int i = GAP; i != 0; i = (i + GAP) % NUMS)
            H.insert(""+i);
        for(int i = GAP; i != 0; i = (i + GAP) % NUMS)
            if(H.insert(""+i))
                System.out.println("OOPS!!! " + i);
        for(int i = 1; i < NUMS; i += 2)
            H.remove(""+i);
        for(int i = 2; i < NUMS; i += 2)
            if(!H.contains(""+i))
                System.out.println("Find fails " + i);
        for(int i = 1; i < NUMS; i += 2) {
            if(H.contains(""+i))
                System.out.println("OOPS!!! " +  i);
        }

        long endTime = System.currentTimeMillis();

        System.out.println("Elapsed time: " + (endTime - startTime));
        System.out.println("H size is: " + H.size());
        System.out.println("Array size is: " + H.capacity());
    }
}


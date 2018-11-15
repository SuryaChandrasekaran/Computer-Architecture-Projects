
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Cache
{
    public static void main(String[] args)
    {
	// TODO Auto-generated method stub
	Scanner sc = new Scanner(System.in);
	System.out.println("enter cache size in KB  among (1, 2, 4, 8, 16, 32)");
	int cacheSize = sc.nextInt();
	System.out.println("enter way associative value among(1,2,4,8)");
	int wayAssociative = sc.nextInt();
	System.out.println("enter cache line size in bytes among(2,4,8,16,32,64)");
	int cacheLineSize = sc.nextInt();
	sc.close();

	long count_hit = 0, count_miss = 0;
	long count_hit_2 = 0, count_miss_2 = 0, clockCycle_2 = 0;
	long count_hit_4 = 0, count_miss_4 = 0, clockCycle_4 = 0;
	long count_hit_8 = 0, count_miss_8 = 0, clockCycle_8 = 0;

	int numberOfCacheLines = (int) ((cacheSize * 1024) / cacheLineSize);

	ArrayList<String> cacheKeys = new ArrayList<String>(numberOfCacheLines);
	ArrayList<String> cacheValues = new ArrayList<String>(numberOfCacheLines);
	ArrayList<String> cacheKeys_0 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheValues_0 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheKeys_1 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheValues_1 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheKeys_2 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheValues_2 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheKeys_3 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheValues_3 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheKeys_4 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheValues_4 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheKeys_5 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheValues_5 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheKeys_6 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheValues_6 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheKeys_7 = new ArrayList<String>(numberOfCacheLines / wayAssociative);
	ArrayList<String> cacheValues_7 = new ArrayList<String>(numberOfCacheLines / wayAssociative);

	long clockCycle = 0;

	StringBuilder y = new StringBuilder("");

	// creating main memory array to store all addresses in a 2d
	// array..mapping one
	// letter into one width length only..1 to 1 mapping.
	// so 2 width is one byte..to get value do block offset*2
	String[][] mainMemory = new String[1743][32];
	for (int i = 0; i < 1743; i++)
	{
	    for (int j = 0; j < 32; j++)
	    {
		mainMemory[i][j] = "";
	    }
	}
	try
	{
	    // read all input files
		String instructionAddresses = new String(
				Files.readAllBytes(Paths
						.get("/Users/akashmalla/Documents/COEN210_Computer_Architecture/project1/inst_addr_trace_hex_project_1.txt")));
		String dataSizes = new String(
				Files.readAllBytes(Paths
						.get("/Users/akashmalla/Documents/COEN210_Computer_Architecture/project1/inst_data_size_project_1.txt")));
		String memoryAddresses = new String(
				Files.readAllBytes(Paths
						.get("/Users/akashmalla/Documents/COEN210_Computer_Architecture/project1/inst_mem_hex_16byte_wide.txt")));
		// copy all memory addresses into the 2d mainMemory array
		// split into each line of address

	    String[] memoryAddress = memoryAddresses.split("\\s+");
	    for (int i = 0; i < 1743; i++)
	    {
		// split address into each letter
		String[] oneBit = memoryAddress[i].split("");

		int z = -1;

		for (int q = oneBit.length - 1; q >= 0; q--)
		{
		    z++;
		    // store each letter into the array as hex
		    mainMemory[i][q] = oneBit[z];
		}
	    }

	    // splitting the instruction addresses into lines
	    String[] instructionAddress = instructionAddresses.split("\\s+");

	    // splitting dataSizes to get length of each data
	    String[] dataSize = dataSizes.split("\\s+");
	    for (int i = 0; i < instructionAddress.length; i++)
	    {
		try
		{
		    String binaryIs = "";
		    String binaryOffset = "";
		    String blockNumber = "";
		    if (Integer.parseInt(instructionAddress[i], 16) > 15)
		    {
			binaryIs = Integer.toBinaryString(Integer.parseInt(instructionAddress[i], 16));
			binaryOffset = binaryIs.substring(binaryIs.length() - 4);
			blockNumber = binaryIs.substring(0, binaryIs.length() - 4);
		    } else
		    {

			blockNumber = "0";
			binaryOffset = Integer.toBinaryString(Integer.parseInt(instructionAddress[i], 16));
		    }

		    // check if hit or miss for 1 way
		    if (wayAssociative == 1)
		    {

			if (cacheKeys.contains(blockNumber + "," + binaryOffset))
			{
			    // cache hit situation
			    count_hit++;
			    clockCycle++;

			}

			else
			{
			    // cache miss
			    count_miss++;
			    clockCycle += 15;

			    // replacement for direct mapping.

			    int newBlockNumber_Value = Integer.parseInt(blockNumber, 2);
			    int newOffset_StartingPosition = (Integer.parseInt(binaryOffset, 2) * 2);
			    int length = Integer.parseInt(dataSize[i]);

			    y.append(mainMemory[newBlockNumber_Value][newOffset_StartingPosition]);

			    int p = -1, beta = 0;
			    for (int alpha = 1; alpha < length; alpha++)
			    {

				if ((alpha + newOffset_StartingPosition) % 31 == 0)
				{

				    newBlockNumber_Value++;
				    newOffset_StartingPosition = 0;
				    beta = length - alpha;

				    if (p < beta)
				    {
					p++;
					y.append(mainMemory[newBlockNumber_Value][p + newOffset_StartingPosition]);
				    }

				} else
				{
				    y.append(mainMemory[newBlockNumber_Value][alpha + newOffset_StartingPosition]);
				}
			    }

			    y = y.reverse();

			    // if y is more than one byte then put it in next cache line
			    if (y.length() > 4)
			    {

				for (int o = 0; o < y.length(); o = o + 4)
				{
				    String yy = y.substring(o, o + 4);

				    cacheKeys.add(blockNumber + "," + binaryOffset);
				    cacheValues.add(yy);

				}
			    } else
			    {

				if (cacheKeys.size() <= (numberOfCacheLines))
				{
				    cacheKeys.add(blockNumber + "," + binaryOffset);
				    cacheValues.add(y.toString());
				} else
				{
				    // System.out.println("cache is full...");

				}

			    }

			}

			y.delete(0, y.length());

		    }

		    else if (wayAssociative > 1)
		    {

			if (wayAssociative == 2)
			{

			    if (cacheKeys_0.contains(blockNumber + "," + binaryOffset)
				    || (cacheKeys_1.contains(blockNumber + "," + binaryOffset)))
			    {
				// cache hit situation
				count_hit_2++;
				clockCycle_2++;

			    }

			    else
			    {
				// cache miss
				count_miss_2++;
				clockCycle_2 += 15;

				// replacement
				int newBlockNumber_Value = Integer.parseInt(blockNumber, 2);
				int newOffset_StartingPosition = (Integer.parseInt(binaryOffset, 2) * 2);
				int length = Integer.parseInt(dataSize[i]);

				y.append(mainMemory[newBlockNumber_Value][newOffset_StartingPosition]);

				int p = -1, beta = 0;
				for (int alpha = 1; alpha < length; alpha++)
				{

				    if ((alpha + newOffset_StartingPosition) % 31 == 0)
				    {

					newBlockNumber_Value++;
					newOffset_StartingPosition = 0;
					beta = length - alpha;

					if (p < beta)
					{
					    p++;
					    y.append(mainMemory[newBlockNumber_Value][p + newOffset_StartingPosition]);
					}

				    } else
				    {
					y.append(mainMemory[newBlockNumber_Value][alpha + newOffset_StartingPosition]);
				    }
				}

				y = y.reverse();

				if (i % wayAssociative == 0)
				{
				    if (cacheKeys.size() <= (numberOfCacheLines))
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {

						String yy = y.substring(o, o + 4);

						if (cacheKeys.size() <= numberOfCacheLines)
						{
						    cacheKeys_0.add(blockNumber + "," + binaryOffset);
						    cacheValues_0.add(yy);
						}

					    }
					} else
					{

					    cacheKeys_0.add(blockNumber + "," + binaryOffset);
					    cacheValues_0.add(y.toString());
					}
				    } else
				    {
					// System.out.println("cache is full...");

				    }

				}

				else if (i % wayAssociative == 1)
				{
				    if (cacheKeys.size() <= (numberOfCacheLines))
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_1.add(blockNumber + "," + binaryOffset);
						cacheValues_1.add(yy);

					    }
					}
					{

					    cacheKeys_1.add(blockNumber + "," + binaryOffset);
					    cacheValues_1.add(y.toString());

					}
				    }

				    else
				    {
					// System.out.println("cache is full...");

				    }

				}

			    }

			    y.delete(0, y.length());

			}

			// way =4
			if (wayAssociative == 4)
			{

			    if (cacheKeys_0.contains(blockNumber + "," + binaryOffset)
				    || cacheKeys_1.contains(blockNumber + "," + binaryOffset)
				    || cacheKeys_2.contains(blockNumber + "," + binaryOffset)
				    || cacheKeys_3.contains(blockNumber + "," + binaryOffset))
			    {
				// cache hit situation
				count_hit_4++;
				clockCycle_4++;

			    }

			    else
			    {
				// cache miss
				count_miss_4++;
				clockCycle_4 += 15;

				// replacement
				int newBlockNumber_Value = Integer.parseInt(blockNumber, 2);
				int newOffset_StartingPosition = (Integer.parseInt(binaryOffset, 2) * 2);
				int length = Integer.parseInt(dataSize[i]);

				y.append(mainMemory[newBlockNumber_Value][newOffset_StartingPosition]);

				int p = -1, beta = 0;
				for (int alpha = 1; alpha < length; alpha++)
				{

				    if ((alpha + newOffset_StartingPosition) % 31 == 0)
				    {

					newBlockNumber_Value++;
					newOffset_StartingPosition = 0;
					beta = length - alpha;

					if (p < beta)
					{
					    p++;
					    y.append(mainMemory[newBlockNumber_Value][p + newOffset_StartingPosition]);
					}

				    } else
				    {
					y.append(mainMemory[newBlockNumber_Value][alpha + newOffset_StartingPosition]);
				    }
				}

				y = y.reverse();

				if (cacheKeys.size() <= (numberOfCacheLines))
				{

				    if (i % wayAssociative == 0)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_0.add(blockNumber + "," + binaryOffset);
						cacheValues_0.add(yy);

					    }
					} else
					{

					    cacheKeys_0.add(blockNumber + "," + binaryOffset);
					    cacheValues_0.add(y.toString());

					}
				    }

				    // mod 1
				    else if (i % wayAssociative == 1)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_1.add(blockNumber + "," + binaryOffset);
						cacheValues_1.add(yy);

					    }
					} else
					{

					    cacheKeys_1.add(blockNumber + "," + binaryOffset);
					    cacheValues_1.add(y.toString());
					}
				    }

				    // mod 2
				    else if (i % wayAssociative == 2)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_2.add(blockNumber + "," + binaryOffset);
						cacheValues_2.add(yy);

					    }
					} else
					{

					    if (cacheKeys.size() <= (numberOfCacheLines))
					    {
						cacheKeys_2.add(blockNumber + "," + binaryOffset);
						cacheValues_2.add(y.toString());
					    } else
					    {
						// System.out.println("cache is full...");

					    }

					}

				    }

				    // mmod 3
				    else if (i % wayAssociative == 3)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_3.add(blockNumber + "," + binaryOffset);
						cacheValues_3.add(yy);

					    }
					} else
					{

					    if (cacheKeys.size() <= (numberOfCacheLines))
					    {
						cacheKeys_3.add(blockNumber + "," + binaryOffset);
						cacheValues_3.add(y.toString());
					    }

					}

				    }

				}

				else
				{
				    // System.out.println("cache is full...");

				}

			    }

			    y.delete(0, y.length());

			}

			else if (wayAssociative == 8)
			{

			    if (cacheKeys_0.contains(blockNumber + "," + binaryOffset)
				    || (cacheKeys_1.contains(blockNumber + "," + binaryOffset))
				    || (cacheKeys_3.contains(blockNumber + "," + binaryOffset))
				    || (cacheKeys_3.contains(blockNumber + "," + binaryOffset))
				    || cacheKeys_4.contains(blockNumber + "," + binaryOffset)
				    || (cacheKeys_5.contains(blockNumber + "," + binaryOffset))
				    || (cacheKeys_7.contains(blockNumber + "," + binaryOffset))
				    || (cacheKeys_6.contains(blockNumber + "," + binaryOffset)))
			    {
				// cache hit situation
				count_hit_8++;
				clockCycle_8++;

			    }

			    else
			    {
				// cache miss
				count_miss_8++;
				clockCycle_8 += 15;

				// replacement

				int newBlockNumber_Value = Integer.parseInt(blockNumber, 2);
				int newOffset_StartingPosition = (Integer.parseInt(binaryOffset, 2) * 2);
				int length = Integer.parseInt(dataSize[i]);

				y.append(mainMemory[newBlockNumber_Value][newOffset_StartingPosition]);

				int p = -1, beta = 0;
				for (int alpha = 1; alpha < length; alpha++)
				{

				    if ((alpha + newOffset_StartingPosition) % 31 == 0)
				    {

					newBlockNumber_Value++;
					newOffset_StartingPosition = 0;
					beta = length - alpha;

					if (p < beta)
					{
					    p++;
					    y.append(mainMemory[newBlockNumber_Value][p + newOffset_StartingPosition]);
					}

				    } else
				    {
					y.append(mainMemory[newBlockNumber_Value][alpha + newOffset_StartingPosition]);
				    }
				}

				y = y.reverse();

				if (cacheKeys.size() <= (numberOfCacheLines))
				{

				    if (i % wayAssociative == 0)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_0.add(blockNumber + "," + binaryOffset);
						cacheValues_0.add(yy);

					    }
					} else
					{

					    cacheKeys_0.add(blockNumber + "," + binaryOffset);
					    cacheValues_0.add(y.toString());

					}
				    }

				    // mod 1
				    else if (i % wayAssociative == 1)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_1.add(blockNumber + "," + binaryOffset);
						cacheValues_1.add(yy);

					    }
					} else
					{

					    cacheKeys_1.add(blockNumber + "," + binaryOffset);
					    cacheValues_1.add(y.toString());
					}
				    }

				    // mod 2
				    else if (i % wayAssociative == 2)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_2.add(blockNumber + "," + binaryOffset);
						cacheValues_2.add(yy);

					    }
					} else
					{

					    if (cacheKeys.size() <= (numberOfCacheLines))
					    {
						cacheKeys_2.add(blockNumber + "," + binaryOffset);
						cacheValues_2.add(y.toString());
					    } else
					    {
						// System.out.println("cache is full...");

					    }

					}

				    }

				    // mmod 3
				    else if (i % wayAssociative == 3)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_3.add(blockNumber + "," + binaryOffset);
						cacheValues_3.add(yy);

					    }
					} else
					{

					    if (cacheKeys.size() <= (numberOfCacheLines))
					    {
						cacheKeys_3.add(blockNumber + "," + binaryOffset);
						cacheValues_3.add(y.toString());
					    }

					}

				    }

				    // 4
				    else if (i % wayAssociative == 4)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_4.add(blockNumber + "," + binaryOffset);
						cacheValues_4.add(yy);

					    }
					} else
					{

					    if (cacheKeys.size() <= (numberOfCacheLines))
					    {
						cacheKeys_4.add(blockNumber + "," + binaryOffset);
						cacheValues_4.add(y.toString());
					    }

					}

				    }

				    // 5
				    else if (i % wayAssociative == 5)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_5.add(blockNumber + "," + binaryOffset);
						cacheValues_5.add(yy);

					    }
					} else
					{

					    if (cacheKeys.size() <= (numberOfCacheLines))
					    {
						cacheKeys_5.add(blockNumber + "," + binaryOffset);
						cacheValues_5.add(y.toString());
					    }

					}

				    }

				    // 6
				    else if (i % wayAssociative == 6)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_6.add(blockNumber + "," + binaryOffset);
						cacheValues_6.add(yy);

					    }
					} else
					{

					    if (cacheKeys.size() <= (numberOfCacheLines))
					    {
						cacheKeys_6.add(blockNumber + "," + binaryOffset);
						cacheValues_6.add(y.toString());
					    }

					}

				    }

				    // 7

				    else if (i % wayAssociative == 7)
				    {
					if (y.length() > 4)
					{

					    for (int o = 0; o < y.length(); o = o + 4)
					    {
						String yy = y.substring(o, o + 4);

						cacheKeys_7.add(blockNumber + "," + binaryOffset);
						cacheValues_7.add(yy);

					    }
					} else
					{

					    if (cacheKeys.size() <= (numberOfCacheLines))
					    {
						cacheKeys_7.add(blockNumber + "," + binaryOffset);
						cacheValues_7.add(y.toString());
					    }

					}

				    }

				    //
				}

				else
				{
				    // System.out.println("cache is full...");

				}

			    }

			    y.delete(0, y.length());

			}

		    }

		    // 3

		} catch (Exception e)
		{
		    e.printStackTrace();
		}

	    }

	    // to print outputs

	    if (wayAssociative == 1)
	    {
		long cache_access = count_hit + count_miss;

		System.out.println("Total number of cache accesses is " + cache_access);
		System.out.println("Total number of clock cycles is " + clockCycle);
		System.out.println("Total number of hits  " + count_hit);

		double hit_ratio = ((double) count_hit) / (count_hit + count_miss) * 100;

		System.out.println("cache hit ratio is " + hit_ratio + "%");

		double ipc = ((double) instructionAddress.length) / clockCycle;
		System.out.println("IPC is " + ipc);
	    }

	    else if (wayAssociative == 2)
	    {
		long cache_access = count_hit_2 + count_miss_2;

		System.out.println("Total number of cache accesses is " + cache_access);
		System.out.println("Total number of clock cycles is " + clockCycle_2);
		System.out.println("Total number of hits  " + count_hit_2);

		double hit_ratio = ((double) count_hit_2) / (count_hit_2 + count_miss_2) * 100;

		System.out.println("cache hit ratio is " + hit_ratio + "%");

		double ipc = ((double) instructionAddress.length) / clockCycle_2;
		System.out.println("IPC is " + ipc);
	    }

	    else if (wayAssociative == 4)
	    {
		long cache_access = count_hit_4 + count_miss_4;

		System.out.println("Total number of cache accesses is " + cache_access);
		System.out.println("Total number of clock cycles is " + clockCycle_4);
		System.out.println("Total number of hits  " + count_hit_4);

		double hit_ratio = ((double) count_hit_4) / (count_hit_4 + count_miss_4) * 100;

		System.out.println("cache hit ratio is " + hit_ratio + "%");

		double ipc = ((double) instructionAddress.length) / clockCycle_4;
		System.out.println("IPC is " + ipc);

	    } else if (wayAssociative == 8)
	    {
		long cache_access = count_hit_8 + count_miss_8;

		System.out.println("Total number of cache accesses is " + cache_access);
		System.out.println("Total number of clock cycles is " + clockCycle_8);
		System.out.println("Total number of hits  " + count_hit_8);

		double hit_ratio = ((double) count_hit_8) / (count_hit_8 + count_miss_8) * 100;

		System.out.println("cache hit ratio is " + hit_ratio + "%");

		double ipc = ((double) instructionAddress.length) / clockCycle_8;
		System.out.println("IPC is " + ipc);
	    }

	} catch (Exception e)
	{
	    e.printStackTrace();
	}

    }
}


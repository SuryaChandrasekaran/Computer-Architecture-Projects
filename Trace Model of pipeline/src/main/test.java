package main;

/**
 * 
 */
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class test
{
    private static long data_count_load_miss=0,data_count_store_miss=0,data_count_load_hit=0,data_count_store_hit=0,clockcycle = 0, newclockcycle = 0, fetch_clock = 0, decode_clock = 0, ex_clock, mem_clock = 0,
	    wb_clock = 0, prevClockCycle = 0;
    static int checkvardatacachemiss;
    // for number of instructions of each type
    private static long alu_count = 0, branch_count = 0, load_count = 0, store_count = 0, jump_count = 0, jal_count = 0,
	    jalr_count = 0, lui_count = 0, aui_pc_count = 0, system_count = 0;
    private static long alu_forwarding_count = 0, mem_forwarding_count = 0, stall_count = 0;
    private static long inst_count_miss = 0, inst_count_hit = 0, data_count_miss = 0, data_count_hit = 0,
	    branch_taken_count = 0;
    private static long branch_state = 0;
    private static long reg_dest = -1l, reg_s1 = -1l, reg_s2 = -1l, prev_reg_dest = -1l;
    private static String inst_type = "";
    private static long[][] scoreboard = new long[32][5];
    private static String prev_inst = null;

    public static String fetch(String instruction_data, String pc_value, String prev_pc_value, int i)
    {
	// fetch stage..
	String i1 = Long.toBinaryString(Long.parseLong(instruction_data, 16));
	long pc = Long.parseLong(pc_value, 16);
	long pc_prev = 0;
	if (i > 0)
	{
	    pc_prev = (Long.parseLong(prev_pc_value, 16));
	} else
	{
	    pc_prev = pc;
	}
	// convert it into 32 bit format and append zeros in the front
	i1 = String.format("%32s", i1).replace(' ', '0');
	clockcycle = fetch_clock;
	prevClockCycle = clockcycle;
	clockcycle++;
	if (clockcycle % 128 == 55)
	{
	    // inst cache miss
	    inst_count_miss++;
	    clockcycle += 15;
	} else
	{
	    inst_count_hit++;
	}
	if (pc - pc_prev > 4)
	{
	    branch_taken_count++;
//	    System.out.println("branch state value is " + branch_state);
	    clockcycle = branch_state += 1;
	} else
	{
	    fetch_clock = Math.max(fetch_clock + 1, decode_clock);
	}
	fetch_clock = clockcycle;
	// System.out.println("clock at end of fetch is "+ clockcycle);
	return i1;
    }

    public static void decode(String i1, String prev_inst, int i)
    {
	// get the 234 bits of temp
	String rd = "";
	String rs1 = "";
	String rs2 = "";
	// String imm = "";
	decode_clock++;
	prevClockCycle = clockcycle;
	clockcycle++;
	if (clockcycle >= fetch_clock)
	{
	    String bits2to4 = i1.substring(27, 30);
	    // get 5,6 bit of temp
	    String bits56 = i1.substring(25, 27);
	    if (bits2to4.equals("000"))
	    {
		if (bits56.equals("00"))
		{
		    // load inst
		    inst_type = "load";
		    load_count++;
		    rd = i1.substring(20, 25);
		    rs1 = i1.substring(12, 17);
		    // imm = i1.substring(0, 12);
		    reg_dest = Long.parseLong(rd, 2);
		    reg_s1 = Long.parseLong(rs1, 2);
		    // reg_imm = Long.parseLong(imm, 2);
		} else if (bits56.equals("01"))
		{
		    // store inst
		    inst_type = "store";
		    store_count++;
		    // base reg
		    rs1 = i1.substring(12, 17);
		    // src reg
		    rs2 = i1.substring(7, 12);
		    // imm = i1.substring(0, 7);
		    reg_s1 = Long.parseLong(rs1, 2);
		    // reg_imm = Long.parseLong(imm, 2);
		    reg_s2 = Long.parseLong(rs2, 2);
		} else if (bits56.equals("11"))
		{
		    // branch inst
		    inst_type = "branch";
		    branch_count++;
		}
	    } else if (bits2to4.equals("001") && bits56.equals("11"))
	    {
		// jalr
		inst_type = "jalr";
		jalr_count++;
		rd = i1.substring(20, 25);
		rs1 = i1.substring(12, 17);
		reg_dest = Long.parseLong(rd, 2);
		reg_s1 = Long.parseLong(rs1, 2);
	    } else if (bits2to4.equals("010") && bits56.equals("11"))
	    {
		// j
		inst_type = "jump";
		jump_count++;
	    } else if (bits2to4.equals("011") && bits56.equals("11"))
	    {
		// jal
		inst_type = "jal";
		jal_count++;
		rd = i1.substring(20, 25);
		reg_dest = Long.parseLong(rd, 2);
	    } else if (bits2to4.equals("100"))
	    {
		if (bits56.equals("00"))
		{
		    // op-imm
		    inst_type = "op-imm";
		    alu_count++;
		    rd = i1.substring(20, 25);
		    rs1 = i1.substring(12, 17);
		    // imm = i1.substring(0, 12);
		    reg_dest = Long.parseLong(rd, 2);
		    reg_s1 = Long.parseLong(rs1, 2);
		    // reg_imm = Long.parseLong(imm, 2);
		} else if (bits56.equals("01"))
		{
		    // op
		    inst_type = "op";
		    alu_count++;
		    rd = i1.substring(20, 25);
		    rs1 = i1.substring(12, 17);
		    rs2 = i1.substring(7, 12);
		    reg_dest = Long.parseLong(rd, 2);
		    reg_s1 = Long.parseLong(rs1, 2);
		    reg_s2 = Long.parseLong(rs2, 2);
		} else if (bits56.equals("11"))
		{
		    // system==branch
		    inst_type = "branch";
		    branch_count++;
		}
	    } else if (bits2to4.equals("101"))
	    {
		if (bits56.equals("00"))
		{
		    // aui-pc
		    inst_type = "aui-pc";
		    aui_pc_count++;
		    rd = i1.substring(20, 25);
		    reg_dest = Long.parseLong(rd, 2);
		} else if (bits56.equals("01"))
		{
		    // lui
		    inst_type = "lui";
		    lui_count++;
		    rd = i1.substring(20, 25);
		    reg_dest = Long.parseLong(rd, 2);
		} else if (bits56.equals("11"))
		{
		    // system
		    inst_type = "branch";
		    branch_count++;
		}
	    } else if (bits2to4.equals("110"))
	    {
		if (bits56.equals("00"))
		{
		    inst_type = "alu-imm";
		    alu_count++;
		    rd = i1.substring(20, 25);
		    rs1 = i1.substring(12, 17);
		    // imm = i1.substring(0, 12);
		    reg_dest = Long.parseLong(rd, 2);
		    reg_s1 = Long.parseLong(rs1, 2);
		    // reg_imm = Long.parseLong(imm, 2);
		} else if (bits56.equals("01"))
		{
		    inst_type = "alu-op";
		    alu_count++;
		    rd = i1.substring(20, 25);
		    rs1 = i1.substring(12, 17);
		    rs2 = i1.substring(7, 12);
		    reg_dest = Long.parseLong(rd, 2);
		    reg_s1 = Long.parseLong(rs1, 2);
		    reg_s2 = Long.parseLong(rs2, 2);
		}
	    } else
	    {
		// nop inst
		inst_type = "nop";
	    }
	}
	// System.out.println("inst type is " + inst_type);
	// System.out.println("reg destination is:" + reg_dest);
	// System.out.println("source 1 is "+ reg_s1);
	// System.out.println("source 2 is "+ reg_s2);
//	System.out.println("the reg destination is " + reg_dest);
	prev_reg_dest = findPrevRegDest(prev_inst, i);
	if (checkDependancy(prev_inst) && reg_dest == prev_reg_dest)
	{
	    clockcycle++;
	}
	if ((reg_dest != 0))
	{
	    setScoreboard(reg_dest, 4, clockcycle);
	}
//	System.out.println("clock at end of decode is " + clockcycle);
    }

    public static long findPrevRegDest(String i1, int i)
    {
	String rd = "";
	i1 = Long.toBinaryString(Long.parseLong(i1, 16));
	i1 = String.format("%32s", i1).replace(' ', '0');
	if (i <= 0)
	{
	    return 0;
	} else
	{
	    String bits2to4 = i1.substring(27, 30);
	    // get 5,6 bit of temp
	    String bits56 = i1.substring(25, 27);
	    if (bits2to4.equals("000"))
	    {
		if (bits56.equals("00"))
		{
		    // load inst
		    rd = i1.substring(20, 25);
		    //
		    prev_reg_dest = Long.parseLong(rd, 2);
		} else if (bits56.equals("01"))
		{
		    // store inst
		} else if (bits56.equals("11"))
		{
		    // branch inst
		    // inst_type = "branch";
		}
	    } else if (bits2to4.equals("001") && bits56.equals("11"))
	    {
		// jalr
		rd = i1.substring(20, 25);
		prev_reg_dest = Long.parseLong(rd, 2);
	    } else if (bits2to4.equals("010") && bits56.equals("11"))
	    {
		// j
		// inst_type = "jump";
	    } else if (bits2to4.equals("011") && bits56.equals("11"))
	    {
		// jal
		// inst_type = "jal";
		// jal_count++;
		rd = i1.substring(20, 25);
		prev_reg_dest = Long.parseLong(rd, 2);
	    } else if (bits2to4.equals("100"))
	    {
		if (bits56.equals("00"))
		{
		    // op-imm
		    rd = i1.substring(20, 25);
		    prev_reg_dest = Long.parseLong(rd, 2);
		} else if (bits56.equals("01"))
		{
		    // op
		    // inst_type = "op";
		    rd = i1.substring(20, 25);
		    prev_reg_dest = Long.parseLong(rd, 2);
		} else if (bits56.equals("11"))
		{
		    // system==branch
		    rd = i1.substring(20, 25);
		    prev_reg_dest = Long.parseLong(rd, 2);
		}
	    } else if (bits2to4.equals("101"))
	    {
		if (bits56.equals("00"))
		{
		    rd = i1.substring(20, 25);
		    prev_reg_dest = Long.parseLong(rd, 2);
		} else if (bits56.equals("01"))
		{
		    rd = i1.substring(20, 25);
		    prev_reg_dest = Long.parseLong(rd, 2);
		} else if (bits56.equals("11"))
		{
		}
	    } else if (bits2to4.equals("110"))
	    {
		if (bits56.equals("00"))
		{
		    rd = i1.substring(20, 25);
		    prev_reg_dest = Long.parseLong(rd, 2);
		} else if (bits56.equals("01"))
		{
		    rd = i1.substring(20, 25);
		    prev_reg_dest = Long.parseLong(rd, 2);
		}
	    } else
	    {
	    }
	}
//	System.out.println("reg destination is:" + prev_reg_dest);
	return prev_reg_dest;
    }

    public static void execute(String i1)
    {
	ex_clock++;
	prevClockCycle = clockcycle;
	clockcycle++;
	// Set scoreboard
	if (inst_type.equals("load") || inst_type.equals("store"))
	{
	    setScoreboard(reg_dest, 2, -1);
	} else if ((reg_dest != 0))
	{
	    setScoreboard(reg_dest, 2, clockcycle);
	}
//	System.out.println("clock at end of execute is " + clockcycle);
    }

    public static void memory(String i1)
    {
	mem_clock++;
	prevClockCycle = clockcycle;
	clockcycle++;
	if (clockcycle % 32 == 11)
	{
	    
	    if(inst_type.equals("load"))
	    {
	    // data cache miss
	   
	    data_count_load_miss++;
	    
	}
	    else if(inst_type.equals("store")) {
		 data_count_store_miss++; }

	    data_count_miss++;
	    clockcycle += 15;
	    fetch_clock = clockcycle;
	}
	  
	else
	{  
	    if(inst_type.equals("load"))
	    {
	    // data cache miss
	   
	    data_count_load_hit++;
	    
	}
	    else if(inst_type.equals("store")) {
		 data_count_store_hit++; }
	    
	    // cache hit
	    data_count_hit++;
	}
	branch_state = clockcycle;
	// Set scoreboard.. assume branch taken will goto memory stage to get target
	// address. adder is present in this stage
	if (reg_dest != 0)
	{
	    setScoreboard(reg_dest, 3, clockcycle);
	}
//	System.out.println("clock at end of memory is " + clockcycle);
    }

    public static void writeBack(String i1)
    {
	wb_clock++;
	prevClockCycle = clockcycle;
	clockcycle++;
	if ((reg_dest != 0))
	{
	    setScoreboard(reg_dest, 1, clockcycle);
	}
//	System.out.println("clock at end of WB is " + clockcycle);
    }

    public static void setScoreboard(long registerNumber, long pipelineStage, long clock_val)
    {
	scoreboard[(int) registerNumber][(int) pipelineStage] = clock_val;
    }

    public static boolean checkDependancy(String i)
    {
//	System.out.println("inside check dependency...");
	// 2=ex stage, 3=mem stage
	if (inst_type.equals("op") || inst_type.equals("alu-op"))
	{
	    if (scoreboard[(int) reg_s1][2] == clockcycle || scoreboard[(int) reg_s2][2] == clockcycle)
	    {
		// stallscoreboard[(int) reg_s1][2]==clockcycle || scoreboard[(int)
		// reg_s1][3]==clockcycle
		// stall_count++;
		alu_forwarding_count++;
		// setScoreboard(reg_dest, 4, clockcycle++);
		return true;
	    }
	    // preceeded by load inst
	    else if (scoreboard[(int) reg_s1][2] == -1 && scoreboard[(int) reg_s1][3] == clockcycle + 1)
	    {
		// stallscoreboard[(int) reg_s1][2]==clockcycle || scoreboard[(int)
		// reg_s1][3]==clockcycle
		stall_count++;
		mem_forwarding_count++;
		setScoreboard(reg_dest, 4, clockcycle++);
		return true;
	    }
	}
	// check for op-imm
	else if (inst_type.equals("op-imm") || inst_type.equals("alu-imm"))
	{
	    if (scoreboard[(int) reg_s1][2] == clockcycle)
	    {
		alu_forwarding_count++;
		// stallscoreboard[(int) reg_s1][2]==clockcycle || scoreboard[(int)
		// reg_s1][3]==clockcycle
		// setScoreboard(reg_dest, 4, clockcycle++);
		return true;
	    }
	    // going 2 times so, we are updating all values so need to check this only if
	    // prev inst is load/store
	    else if (scoreboard[(int) reg_s1][2] == -1 && scoreboard[(int) reg_s1][3] == clockcycle + 1)
	    {
		// stallscoreboard[(int) reg_s1][2]==clockcycle || scoreboard[(int)
		// reg_s1][3]==clockcycle
		stall_count++;
		mem_forwarding_count++;
		setScoreboard(reg_dest, 4, clockcycle++);
		return true;
	    }
	} else if (inst_type.equals("jalr"))
	{
	    if (scoreboard[(int) reg_s1][2] == clockcycle)
	    {
		// stall 1 cycle
		// stallscoreboard[(int) reg_s1][2]==clockcycle || scoreboard[(int)
		// reg_s1][3]==clockcycle
		alu_forwarding_count++;
		// setScoreboard(reg_dest, 4, clockcycle++);
		return true;
	    } else if (scoreboard[(int) reg_s1][2] == -1 && scoreboard[(int) reg_s1][3] == clockcycle + 1)
	    {
		// stallscoreboard[(int) reg_s1][2]==clockcycle || scoreboard[(int)
		// reg_s1][3]==clockcycle
		stall_count++;
		mem_forwarding_count++;
		setScoreboard(reg_dest, 4, clockcycle++);
		return true;
	    }
	} else if (inst_type.equals("load"))
	{
	    if (scoreboard[(int) reg_s1][2] == clockcycle)
	    {
		alu_forwarding_count++;
		// stall 1 cycle
		// stallscoreboard[(int) reg_s1][2]==clockcycle || scoreboard[(int)
		// reg_s1][3]==clockcycle
		// setScoreboard(reg_dest, 4, clockcycle++);
		return true;
	    } else if (scoreboard[(int) reg_s1][2] == -1 && scoreboard[(int) reg_s1][3] == clockcycle + 1)
	    {
		// stallscoreboard[(int) reg_s1][2]==clockcycle || scoreboard[(int)
		// reg_s1][3]==clockcycle
		stall_count++;
		mem_forwarding_count++;
		setScoreboard(reg_dest, 4, clockcycle++);
		return true;
	    }
	}
	// if at that clockcycle it is in ex stage then add 1 bubble, in mem stage then
	// 2 bubbles.
	// same calc for ex and mem forwarding.
	// total stall cycles= number of bubbles added
	if (inst_type.equals("store"))
	{
	    if (scoreboard[(int) reg_s2][2] == clockcycle)
	    {
		alu_forwarding_count++;
		// stall 1 cycle
		// stallscoreboard[(int) reg_s1][2]==clockcycle || scoreboard[(int)
		// reg_s1][3]==clockcycle
		// setScoreboard(reg_dest, 4, clockcycle++);
		return true;
	    } else if (scoreboard[(int) reg_s2][2] == -1 && scoreboard[(int) reg_s2][3] == clockcycle + 1)
	    {
		// stallscoreboard[(int) reg_s1][2]==clockcycle || scoreboard[(int)
		// reg_s1][3]==clockcycle
		stall_count++;
		mem_forwarding_count++;
		setScoreboard(reg_dest, 4, clockcycle++);
		return true;
	    }
	}
	return false;
    }

    /**
     * @param args
     */
    public static void main(String[] args)
    {
	// TODO Auto-generated method stub
	// read input files
	// init all scoreboard values to 0
	for (int i = 0; i < 32; i++)
	{
	    scoreboard[i][0] = i;
	    for (int j = 1; j < 5; j++)
	    {
		scoreboard[i][j] = 0;
	    }
	}
	try
	{
	    String pc_values = new String(Files.readAllBytes(Paths.get(
		    "X:\\\\GROWTH\\\\eclipse workspace\\\\ca_proj2_pipeline\\\\src\\\\main\\\\inst_addr_riscv_trace_project_2.txt")));
	    String instruction_datas = new String(Files.readAllBytes(Paths.get(
		    "X:\\\\GROWTH\\\\eclipse workspace\\\\ca_proj2_pipeline\\\\src\\\\main\\\\inst_data_riscv_trace_project_2.txt")));
	    String[] instruction_data = instruction_datas.split("\\s+");
	    String[] pc_value = pc_values.split("\\s+");
	    /*
	     * Instruction can only be accepted if the current clock cycle is greater or
	     * equal to the time stamp of the instructions from the first pipeline stage and
	     * the last decoded instruction
	     */
	    String instruction = null;
	    for (int i = 0; i < instruction_data.length; i++)
	    {
//		System.out.println("for inst " + (i + 1));
		if (clockcycle >= prevClockCycle)
		{
		    // Run the following at the beginning
		    if (i == 0)
		    {
			instruction = fetch(instruction_data[i], pc_value[i], "0", i);
			decode(instruction, "0", i);
			execute(instruction);
			memory(instruction);
			writeBack(instruction);
		    } else if (i > 0)
		    {
			prev_inst = instruction_data[i - 1];
//			System.out.println(prev_inst);
			instruction = fetch(instruction_data[i], pc_value[i], pc_value[i - 1], i);
			if (i < instruction_data.length - 1)
			{
			    decode(instruction, prev_inst, i);
			} else
			{
			    decode(instruction, prev_inst, i);
			}
			// if nop then goto next inst
			if (inst_type.equals("nop"))
			{
			    continue;
			}
			// see if dependency then overwrite.. 4, does 1 stall
			boolean dep = checkDependancy(instruction);
//			System.out.println("clock at end of dependency is " + clockcycle);
			execute(instruction);
			memory(instruction);
			writeBack(instruction);
			// }
		    }
		}
		// System.out.println(Arrays.deepToString(scoreboard));
	    }
	    long inst_cache_accesses = inst_count_hit + inst_count_miss;
	    long data_cache_accesses = data_count_load_hit+data_count_load_miss;
	    long data_cache_store_accesses = data_count_store_hit+data_count_store_miss;
	    long data_cache_load_accesses = data_count_load_hit+data_count_load_miss;
	    // printing statistics
	    System.out.println("total inst cache accesses " + inst_cache_accesses);
//	    System.out.println("total data cache  accesses " + data_cache_accesses);
	    System.out.println("total data cache  load accesses " + data_cache_load_accesses);
	    System.out.println("total data cache  store accesses " + data_cache_store_accesses);
	    System.out.println("inst cache hits " + inst_count_hit);
	    System.out.println("data cache hits " + data_count_hit);
	    System.out.println("total alu insructions is " + alu_count);
	    System.out.println("total branch insructions is " + branch_count);
	    System.out.println("total load insructions is " + load_count);
	    System.out.println("total store insructions is " + store_count);
	    System.out.println("total jal insructions is " + jal_count);
	    System.out.println("total jalr insructions is " + jalr_count);
	    System.out.println("total aui-pc insructions is " + aui_pc_count);
	    System.out.println("total jump insructions is " + jump_count);
	    System.out.println("total lui insructions is " + lui_count);
	    System.out.println("total system insructions is " + system_count);
	    System.out.println("total taken branches is " + branch_taken_count);
	    System.out.println("total number of forwarding from ex stage is " + alu_forwarding_count);
	    System.out.println("total number of forwarding from mem stage is " + mem_forwarding_count);
	    System.out.println("total number of stalled cycles in ID state" + stall_count);
	    System.out.println("total clock cycles is " + clockcycle);
	    double inst_hit_ratio = ((double) inst_count_hit) / (inst_count_hit + inst_count_miss) * 100;
	    double data_hit_ratio = ((double) data_count_hit) / (data_count_hit + data_count_miss) * 100;
	    System.out.println("instruction cache hit ratio is " + inst_hit_ratio+"%");
	    System.out.println("memory cache hit ratio is " + data_hit_ratio+"%");
	    double ipc = ((double) instruction_data.length) / clockcycle;
	    System.out.println("IPC is " + ipc);
	    System.out.println("percentage of taken branches over total branches "
		    + ((double) branch_taken_count / branch_count) * 100+"%");
	    System.out.println("frequency of instructions");
	    
	    Long t1=system_count+branch_count+jump_count+ jal_count+jalr_count;
	    System.out.println("alu instructions: "+   ((double) alu_count / instruction_data.length) * 100 +"%");
	    System.out.println("load instructions: "+   ((double) load_count / instruction_data.length) * 100 +"%");
	    System.out.println("store instructions: "+   ((double) store_count / instruction_data.length) * 100 +"%");
	    System.out.println("branch instructions: "+   ((double) t1 / instruction_data.length) * 100 +"%" );
	    System.out.println("total aui-pc insructions is " + ((double) aui_pc_count / instruction_data.length) * 100+"%");
	    System.out.println("total lui insructions is " + ((double) lui_count / instruction_data.length) * 100+"%");
	   
	    
	    
	    System.out.println(
		    "percentage of stalled cycles over total cycles " + ((double) stall_count / clockcycle) * 100+"%");
//	   final state of Scoreboard
	    System.out.println(Arrays.deepToString(scoreboard));
	} catch (Exception e)
	{
	    // TODO: handle exception
	    e.printStackTrace();
	}
    }
}
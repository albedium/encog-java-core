package org.encog.persist;

import java.io.File;
import java.io.IOException;
import java.util.List;

import junit.framework.Assert;

import org.encog.ml.ea.genome.Genome;
import org.encog.ml.ea.species.Species;
import org.encog.ml.prg.EncogProgram;
import org.encog.ml.prg.EncogProgramContext;
import org.encog.ml.prg.extension.StandardExtensions;
import org.encog.ml.prg.train.PrgPopulation;
import org.encog.parse.expression.common.RenderCommonExpression;
import org.encog.util.TempDir;
import org.encog.util.obj.SerializeObject;
import org.junit.After;
import org.junit.Test;

public class TestPersistEPLPopulation {
	public final TempDir TEMP_DIR = new TempDir();
	public final File EG_FILENAME = TEMP_DIR.createFile("encogtest.eg");
	public final File SERIAL_FILENAME = TEMP_DIR.createFile("encogtest.ser");
		
	private PrgPopulation create()
	{
		EncogProgramContext context = new EncogProgramContext();
		StandardExtensions.createAll(context.getFunctions());
		PrgPopulation pop = new PrgPopulation(context,10);
		EncogProgram prg1 = new EncogProgram(context);
		EncogProgram prg2 = new EncogProgram(context);
		prg1.compileExpression("x+1");
		prg2.compileExpression("(x+5)/2");
		
		Species defaultSpecies = pop.createSpecies();
		defaultSpecies.add(prg1);
		defaultSpecies.add(prg2);
		return pop;
	}
	
	@Test
	public void testPersistEG()
	{
		PrgPopulation pop = create();
		EncogDirectoryPersistence.saveObject(EG_FILENAME, pop);
		PrgPopulation pop2 = (PrgPopulation)EncogDirectoryPersistence.loadObject(EG_FILENAME);
		validate(pop2);
	}
	
	@Test
	public void testPersistSerial() throws IOException, ClassNotFoundException
	{
		PrgPopulation pop = create();
		validate(pop);
		SerializeObject.save(SERIAL_FILENAME, pop);
		PrgPopulation pop2 = (PrgPopulation)SerializeObject.load(SERIAL_FILENAME);
		validate(pop2);		
	}
		
	private void validate(PrgPopulation pop)
	{
		List<Genome> list = pop.flatten();
		Assert.assertEquals(2, list.size());
		
		EncogProgram prg1 = (EncogProgram)list.get(0);
		EncogProgram prg2 = (EncogProgram)list.get(1);
		
		RenderCommonExpression render = new RenderCommonExpression();
		Assert.assertEquals("(x+1)", render.render(prg1));
		Assert.assertEquals("((x+5)/2)", render.render(prg2));
	}

	@After
	public void tearDown() throws Exception {
		TEMP_DIR.dispose();
	}
	
	
}

package bll;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import dao.dto.TutelatDTO;
import jxl.Sheet;
import jxl.Workbook;
import jxl.read.biff.BiffException;
import pojo.AlumneTutor;
import pojo.Grup;
import pojo.Professor;
import pojo.Tutelat;


/*
 * AQUESTA CLASSE S'USA EXCLUSIVAMENT
 * PER A FER LA CARREGA INCIAL DEL SISTEMA
 */
public class CarregaInicial {
	private int maximTutelatsGrup = 11;
	private static CarregaInicial INSTANCIA;

	private CarregaInicial() {

	}

	public CarregaInicial getInstancia() {
		return INSTANCIA;
	}

	/******************************************
	 * LLIG I FA LA TRANSFORMACIO DELS EXCELS
	 * 
	 * @throws IOException
	 * @throws BiffException
	 *****************************************/
	public ArrayList<Professor> carregaInicialProfessors(String path) throws BiffException, IOException {
		ArrayList<Professor> llista = new ArrayList<>();
		Workbook workbook = Workbook.getWorkbook(new File(path));
		Sheet sheet = workbook.getSheet(0);
		//
		for (int i = 0; i < sheet.getRows(); i++) {
			// segons el format del excel dels alumnes nou ingres
			String nif = sheet.getCell(i, 0).getContents();
			String cognoms = sheet.getCell(i, 1).getContents();
			String nom = sheet.getCell(i, 2).getContents();
			String correu = sheet.getCell(i, 3).getContents();

			llista.add(new Professor(nif, nom, cognoms, correu));
		}
		return llista;
	}

	public ArrayList<AlumneTutor> carregaInicialAlumnesTutors(String path) throws BiffException, IOException {
		ArrayList<AlumneTutor> llista = new ArrayList<>();
		Workbook workbook = Workbook.getWorkbook(new File(path));
		Sheet sheet = workbook.getSheet(0);
		//
		for (int i = 0; i < sheet.getRows(); i++) {
			// segons el format del excel dels alumnes nou ingres
			String nif = sheet.getCell(i, 0).getContents();
			String cognoms = sheet.getCell(i, 1).getContents();
			String nom = sheet.getCell(i, 2).getContents();
			String correu = sheet.getCell(i, 3).getContents();

			llista.add(new AlumneTutor(nif, nom, cognoms, correu));
		}
		return llista;
	}

	public ArrayList<TutelatDTO> carrgeaInicialTutelats(String path) throws BiffException, IOException {
		ArrayList<TutelatDTO> llista = new ArrayList<>();
		// obrim el excell
		Workbook workbook = Workbook.getWorkbook(new File(path));
		// obtenim la primera pagina
		Sheet sheet = workbook.getSheet(0);
		// per files
		for (int i = 0; i < sheet.getRows(); i++) {
			// segons el format del excel dels alumnes nou ingres
			String nif = sheet.getCell(i, 0).getContents();
			// cognoms [0], nom [1]
			String nomCognoms[] = sheet.getCell(i, 1).getContents().split(",");
			String mobil = sheet.getCell(i, 2).getContents();
			String correu_upv = sheet.getCell(i, 3).getContents();
			String correu_personal = sheet.getCell(i, 4).getContents();
			//hi han alumnes amb diversos grups, sols volem el primer
			String grupo = sheet.getCell(i, 5).getContents().trim().substring(0, 3);
			// crea l'objecte
			TutelatDTO tutelat = new TutelatDTO(nif, nomCognoms[1].trim(), nomCognoms[0].trim(), correu_upv,
					correu_personal, TutelatDTO.noAsignat, grupo, mobil);
			llista.add(tutelat);
		}
		return llista;
	}

	public void assignarTutelatsGrup() {
		// tot el llistat dels grups
		ArrayList<Grup> grups = Coordina2.getInstancia().llistarGrups();
		// is no està inicialitzat ho fem
		if (grups == null) {
			grups = new ArrayList<>();
		}
		ArrayList<Tutelat> tutelats = Coordina2.getInstancia().llistarTutelats();
		// si l'array està buit no hi ha res a fer
		if (tutelats.size() == 0)
			return;
		// agafem el primer grup per a començar l'algorisme
		String grupMatActual = tutelats.get(0).getGrup_matricula();
		// numero de tutelats assignats; cadinalitat 1er, 2n...
		int numero = 1;
		//mante el grup actual
		Grup actual = new Grup("G01");
		grups.add(actual);
		// bucle que recorre tots els tutelats
		for (Tutelat aux : tutelats) {
			//detectem si ja tenim un grup "complet"
			if(numero>maximTutelatsGrup || (!aux.getGrup_matricula().equals(grupMatActual))){
				//tornem a agafar el primer...
				numero = 1;
				//crea un grup nou
				if(grups.size()<9){
					actual = new Grup("G0"+grups.size());
				}else{
					actual = new Grup("G"+grups.size());
				}
				//afeig eixe grup a l'array
				grups.add(actual);
				//pilla el grup de matricula
				grupMatActual=aux.getGrup_matricula();
			}
			aux.setGrup_patu(actual);
		}
	}
	/*
	 * ELS GRUPS NO ES LLIGEN, SOLS ELS CREEN
	 */
}

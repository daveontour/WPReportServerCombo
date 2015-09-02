package au.com.quaysystems.qrm.server;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import javax.imageio.ImageIO;

import au.com.quaysystems.qrm.wp.model.Matrix;
import au.com.quaysystems.qrm.wp.model.Risk;


public class RelMatrixReportVisitor  {

	public void process( HashMap<Object, Object> taskParamMap, List<Risk> risks, Matrix mat) {
		
		ByteArrayOutputStream array = new ByteArrayOutputStream();

		int state = QRMConstants.CURRENT;

		try {
			if(taskParamMap.containsKey("inherentState") && (Boolean)taskParamMap.get("inherentState")){
				state = QRMConstants.INHERENT;
			}
			if(taskParamMap.containsKey("treatedState") && (Boolean)taskParamMap.get("treatedState")){
				state = QRMConstants.TREATED;
			}
		} catch (Exception e1) {
			state = QRMConstants.CURRENT;
		}

		try {
			ImageIO.write(MatrixPainter.getPNGRelMatrix(mat, 500, 500, risks, state),"png", array);
		} catch (IOException e) {
			e.printStackTrace();
		}
		taskParamMap.put("QRMRELMATRIX", array.toByteArray());
		taskParamMap.put("RELMATRIXSTATE", state);
	}
}

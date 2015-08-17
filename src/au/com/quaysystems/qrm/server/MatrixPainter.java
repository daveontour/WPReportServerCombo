package au.com.quaysystems.qrm.server;


import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.font.FontRenderContext;
import java.awt.font.TextLayout;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;

import au.com.quaysystems.qrm.wp.model.ModelRiskLite;
import au.com.quaysystems.qrm.wp.model.ModelToleranceMatrix;


//import au.com.quaysystems.qrm.QRMConstants;
//import au.com.quaysystems.qrm.dto.ModelDataObjectAllocation;
//import au.com.quaysystems.qrm.dto.ModelRiskLite;
//import au.com.quaysystems.qrm.dto.ModelToleranceMatrix;

public class MatrixPainter {


	private static void drawMat(final ModelToleranceMatrix matrix,
			final int width, final int height, final Graphics2D g2d,
			final ModelRiskLite risk) {

		int maxProb = matrix.getMaxProb();
		int maxImpact = matrix.getMaxImpact();
		int xUnit = (width / maxImpact);
		int yUnit = (height / maxProb);

		int impactI = 0;
		int probI = 0;
		int impactT = 0;
		int probT = 0;

		if (risk != null) {
			impactI = risk.inherentImpact.intValue();
			probI = risk.inherentProb.intValue();
			impactT = risk.treatedImpact.intValue();
			probT =  risk.treatedProb.intValue();
		}

		Font fn = new Font("Arial", Font.BOLD, 12);
		g2d.setFont(fn);
		FontRenderContext frc = g2d.getFontRenderContext();
		TextLayout layout = new TextLayout("X", fn, frc);
		int fnH = (int) (layout.getBounds().getHeight());
		int fnW = (int) (layout.getBounds().getWidth());

		for (int x = 0; x < maxImpact; x++) {
			for (int y = 0; y < maxProb; y++) {
				g2d.setColor(getTolerance(maxProb - y, x + 1, matrix));
				g2d.fillRect((x * xUnit), (y * yUnit), (xUnit), (yUnit));
				g2d.setColor(Color.BLACK);
				g2d.drawRect((x * xUnit), (y * yUnit), (xUnit), (yUnit));

				if (impactI != 0 && probI != 0 && impactI == x + 1	&& probI == maxProb - y) {

					g2d.setColor(Color.WHITE);
					g2d.fillOval((x * xUnit + 3), (y * yUnit + 3), (xUnit - 6),	(yUnit - 6));
					g2d.setColor(Color.BLACK);
					g2d.drawOval((x * xUnit + 3), (y * yUnit + 3), (xUnit - 6),	(yUnit - 6));

					double xC = (x + 0.5) * xUnit;
					double yC = (y + 0.5) * yUnit;

					float textX = new Float(xC - fnW / 2);
					float textY = new Float(yC + fnH / 2);

					g2d.setPaint(Color.RED);
					layout.draw(g2d, textX, textY);

				}

				if (impactT != 0 && probT != 0 && impactT == x + 1
						&& probT == maxProb - y) {

					g2d.setColor(Color.WHITE);
					g2d.fillOval((x * xUnit + 3), (y * yUnit + 3), (xUnit - 6),	(yUnit - 6));
					g2d.setColor(Color.BLACK);
					g2d.drawOval((x * xUnit + 3), (y * yUnit + 3), (xUnit - 6),	(yUnit - 6));

					double xC = (x + 0.5) * xUnit;
					double yC = (y + 0.5) * yUnit;

					float textX = new Float(xC - fnW / 2);
					float textY = new Float(yC + fnH / 2);

					g2d.setPaint(Color.BLUE);
					layout.draw(g2d, textX, textY);
				}
			}
		}
		g2d.setColor(Color.BLACK);
		g2d.drawRect(0, 0, width - 1, height - 1);
	}

//	private static void drawMatAllocations(final ModelToleranceMatrix matrix,
//			final int width, final int height, final Graphics2D g2d,
//			final boolean descendants, final int status, final Long projectID,
//			final List<ModelDataObjectAllocation> summaries, int lft, int rgt, boolean drawAxis) {
//
//		int maxProb = matrix.getMaxProb();
//		int maxImpact = matrix.getMaxImpact();
//		int xUnit = (width / maxImpact);
//		int yUnit = (height / maxProb);
//		Long matrixID = matrix.getMatrixID();
//		
//		int xOffset = 0;
//		int yOffset = 0;
//		
//		if (drawAxis){
//			xUnit = ((width-30) / maxImpact);
//			yUnit = ((height-30) / maxProb);
//			xOffset = 15;
//			yOffset = 15;
//		}
//
//		Integer[][] valMap = new Integer[maxProb][maxImpact];
//		for (int x = 0; x < maxProb; x++) {
//			for (int y = 0; y < maxImpact; y++) {
//				valMap[x][y] = 0;
//			}
//		}
//
//		if (summaries != null){
//			for (ModelDataObjectAllocation alloc : summaries) {
//				if (alloc.getMatrixID() == matrixID && alloc.getFLAG() == status &&
//						(( alloc.getLft() >= lft && alloc.getRgt() <= rgt && descendants) || (alloc.getProjectID() == projectID)) ) {
//					int y = maxProb - alloc.getPROB();
//					int x = alloc.getIMPACT()-1;
//					valMap[y][x] = valMap[y][x]+alloc.getRCOUNT();
//				}
//			}
//		}
//
//		Font fn = new Font("Arial", Font.BOLD, 12);
//		g2d.setFont(fn);
//		FontRenderContext frc = g2d.getFontRenderContext();
//
//		for (int x = 0; x < maxImpact; x++) {
//			for (int y = 0; y < maxProb; y++) {
//				g2d.setColor(getTolerance(maxProb - y, x + 1, matrix));
//				g2d.fillRect((x * xUnit)+xOffset, (y * yUnit)+yOffset, (xUnit), (yUnit));
//				g2d.setColor(Color.BLACK);
//				g2d.drawRect((x * xUnit)+xOffset, (y * yUnit)+yOffset, (xUnit), (yUnit));
//
//				TextLayout layout = null;
//				if (summaries != null) {
//					layout = new TextLayout(new Integer(valMap[y][x]).toString(), fn, frc);
//				} else {
//					layout = new TextLayout("X", fn, frc);
//				}
//				layout.draw(g2d, new Float(((x + 0.5) * xUnit - (int) (layout.getBounds().getWidth()) / 2)+xOffset), new Float(((y + 0.5) * yUnit + (int) (layout.getBounds().getHeight()) / 2))+yOffset);
//			}
//		}
//		if (!drawAxis){
//			g2d.setColor(Color.BLACK);
//			g2d.drawRect(0, 0, width - 1, height - 1);
//		}
//		if (drawAxis){
//
//			// Draw the  x axis title
//			Font fn0 = new Font("Arial", Font.BOLD, 12);
//			g2d.setFont(fn0);
//			FontRenderContext frc0 = g2d.getFontRenderContext();
//			TextLayout layout0 = new TextLayout("Impact", fn0, frc0);
//			int fnW0 = (int) (layout0.getBounds().getWidth());
//			float textX0 = (width/2) - (fnW0/2);
//			float textY0 = height - 2;
//			layout0.draw(g2d, textX0, textY0);
//
//			//Draw the y axis title vertically by applying a Affine Transform to the coordinate system.
//			layout0 = new TextLayout("Probability", fn0, frc0);
//			fnW0 = (int) (layout0.getBounds().getWidth());
//
//			AffineTransform at = new AffineTransform();
//			at.translate(10, (int)((height/2)+(fnW0/2)));
//			at.rotate(-Math.PI/2);
//			g2d.setTransform(at);
//			new TextLayout( "Probability", fn0, frc0).draw(g2d,0,0);
//		}
//
//	}

//	private static void drawMat(final ModelToleranceMatrix matrix,
//			final int width, final int height, final Graphics2D g2d,
//			final ArrayList<ModelRiskLite> risks, final int state) {
//
//		int maxProb = matrix.getMaxProb();
//		int maxImpact = matrix.getMaxImpact();
//
//		int offSetX = 30;
//		int offSetY = 15;
//
//		int xUnit = ((width - 2 * offSetX) / maxImpact);
//		int yUnit = ((height - 2 * offSetY) / maxProb);
//
//		for (int x = 0; x < maxImpact; x++) {
//			for (int y = 0; y < maxProb; y++) {
//				g2d.setColor(getTolerance(maxProb - y, x + 1, matrix));
//				g2d.fillRect((x * xUnit) + offSetX, (y * yUnit) + offSetY,(xUnit), (yUnit));
//				g2d.setColor(Color.BLACK);
//				g2d.drawRect((x * xUnit) + offSetX, (y * yUnit) + offSetY,(xUnit), (yUnit));
//			}
//		}
//		g2d.setColor(Color.BLACK);
//		
//		// Draw the  x axis title
//		Font fn0 = new Font("Arial", Font.BOLD, 12);
//		g2d.setFont(fn0);
//		FontRenderContext frc0 = g2d.getFontRenderContext();
//		TextLayout layout0 = new TextLayout("Impact", fn0, frc0);
//		int fnW0 = (int) (layout0.getBounds().getWidth());
//		float textX0 = (width/2) - (fnW0/2);
//		float textY0 = height - 2;
//		layout0.draw(g2d, textX0, textY0);
//
//
//		if (risks != null) {
//			for (ModelRiskLite risk : risks) {
//
//				Font fn = new Font("Arial", Font.PLAIN, 9);
//				g2d.setFont(fn);
//				FontRenderContext frc = g2d.getFontRenderContext();
//				TextLayout layout = new TextLayout(risk.riskProjectCode, fn, frc);
//				int fnH = (int) (layout.getBounds().getHeight());
//				int fnW = (int) (layout.getBounds().getWidth());
//
//				double impact = 0;
//				double prob = 0;
//
//				switch (state) {
//				case QRMConstants.TREATED:
//					impact = risk.treatedImpact;
//					prob = risk.treatedProb;
//					break;
//				case QRMConstants.INHERENT:
//					impact = risk.inherentImpact;
//					prob = risk.inherentProb;
//					break;
//				case QRMConstants.CURRENT:
//					impact = risk.currentImpact;
//					prob = risk.currentProb;
//					break;
//				default:
//					break;
//				}
//
//				double xC = offSetX + (impact - 1) * xUnit;
//				double yC = offSetY + (matrix.getMaxProb() + 1 - prob) * yUnit;
//
//				g2d.setColor(Color.WHITE);
//				g2d.fillOval((int) (xC - 30), (int) (yC - 15), 60, 30);
//				g2d.setColor(Color.BLACK);
//				g2d.drawOval((int) (xC - 30), (int) (yC - 15), 60, 30);
//
//				float textX = new Float(xC - fnW / 2);
//				float textY = new Float(yC + fnH / 2);
//
//				if (risk.treated) {
//					g2d.setPaint(Color.BLUE);
//				} else {
//					g2d.setPaint(Color.RED);
//				}
//				layout.draw(g2d, textX, textY);
//			}
//		}
//		
//		//Draw the y axis title vertically by applying a Affine Transform to the coordinate system.
//		layout0 = new TextLayout("Probability", fn0, frc0);
//		fnW0 = (int) (layout0.getBounds().getWidth());
//		
//		AffineTransform at = new AffineTransform();
//	    at.translate(20, (int)((height/2)+(fnW0/2)));
//	    at.rotate(-Math.PI/2);
//	    g2d.setTransform(at);
//		g2d.setColor(Color.BLACK);
//		new TextLayout( "Probability", fn0, frc0).draw(g2d,0,0);
//
//	}

	private static Color getTolerance(final int prob, final int impact,	final ModelToleranceMatrix matrix) {
		
		if ((prob > matrix.getMaxProb()) || (prob < 1)	|| (impact > matrix.getMaxImpact()) || (impact < 1)) {
			return Color.WHITE;
		}

		int strIndex = (prob - 1) * (int) matrix.getMaxImpact() + (impact - 1);

		try {
			switch (Integer.parseInt(matrix.getTolString().substring(strIndex, strIndex + 1))) {
			case 1:
				return new Color(28,132,198,255);
			case 2:
				return new Color(26,179,148,255);
			case 3:
				return new Color(255,255,85,255);
			case 4:
				return new Color(248,172,89,255);
			case 5:
				return new Color(237,85,101,255);
			default:
				return Color.WHITE;
			}
		} catch (RuntimeException e) {
			return Color.WHITE;
		}
	}

	public static RenderedImage getPNGSingleRisk(
			final ModelToleranceMatrix mat, final int sizex, final int sizey,
			final ModelRiskLite risk) {

		BufferedImage image = new BufferedImage(sizex, sizey,BufferedImage.TYPE_4BYTE_ABGR);
		Graphics2D g2d = image.createGraphics();
		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

		drawMat(mat, sizex, sizey, g2d, risk);
		return image;
	}


//	public static RenderedImage getAllocationMatrix(
//			final ModelToleranceMatrix mat, final int sizex, final int sizey,
//			final boolean descendants, final int state, final Long projectID,
//			final List<ModelDataObjectAllocation> summaries, int lft, int rgt) {
//
//		BufferedImage image = new BufferedImage(sizex, sizey,BufferedImage.TYPE_4BYTE_ABGR);
//		Graphics2D g2d = image.createGraphics();
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//		drawMatAllocations(mat, sizex, sizey, g2d, descendants, state,projectID, summaries, lft, rgt,false);
//
//		return image;
//	}
//
//	public static RenderedImage getAllocationMatrixWithAxisLabels(
//			final ModelToleranceMatrix mat, final int sizex, final int sizey,
//			final boolean descendants, final int state, final Long projectID,
//			final List<ModelDataObjectAllocation> summaries, int lft, int rgt) {
//
//		BufferedImage image = new BufferedImage(sizex, sizey,BufferedImage.TYPE_4BYTE_ABGR);
//		Graphics2D g2d = image.createGraphics();
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//		drawMatAllocations(mat, sizex, sizey, g2d, descendants, state,projectID, summaries, lft, rgt,true);
//
//		return image;
//	}
//
//	public static RenderedImage drawHighLight(final int diameter) {
//
//		BufferedImage image = new BufferedImage(diameter, diameter,	BufferedImage.TYPE_4BYTE_ABGR);
//		Graphics2D g2d = image.createGraphics();
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//		g2d.setColor(Color.BLUE);
//		g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
//		g2d.fillRect(0, 0, diameter, diameter);
//		return image;
//	}
//
//	public static RenderedImage drawRelMatrixItem(final boolean treated,final boolean highlight, final String id) {
//
//		BufferedImage image = new BufferedImage(65, 35,	BufferedImage.TYPE_4BYTE_ABGR);
//		Graphics2D g2d = image.createGraphics();
//		g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING,RenderingHints.VALUE_ANTIALIAS_ON);
//
//		Font fn = new Font("Arial", Font.BOLD, 9);
//		g2d.setFont(fn);
//		FontRenderContext frc = g2d.getFontRenderContext();
//		TextLayout layout = new TextLayout(id, fn, frc);
//		int fnH = (int) (layout.getBounds().getHeight());
//		int fnW = (int) (layout.getBounds().getWidth());
//
//		if (highlight) {
//			g2d.setColor(Color.LIGHT_GRAY);
//		} else {
//			g2d.setColor(Color.WHITE);
//		}
//		g2d.fillOval(0, 0, 60, 30);
//		g2d.setColor(Color.BLACK);
//		g2d.drawOval(0, 0, 60, 30);
//
//		float textX = new Float(30 - fnW / 2);
//		float textY = new Float(15 + fnH / 2);
//
//		if (treated) {
//			g2d.setPaint(Color.BLUE);
//		} else {
//			g2d.setPaint(Color.RED);
//		}
//
//		if (highlight) {
//			g2d.setPaint(Color.BLACK);
//		}
//
//		layout.draw(g2d, textX, textY);
//
//		return image;
//	}
}

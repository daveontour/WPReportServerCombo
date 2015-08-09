package au.com.quaysystems.qrm.wp;
public class QRMConstants {

	
	public static final long QRM_FAULT = 0;
	public static final long QRM_OK = 1;

	public static final int HTML = 0;
	public static final int PDF = 1;
	public static final int DOWNLOAD = 2;
	public static final int DOC = 3;
	public static final int PPT = 4;
	public static final int XLS = 5;

	public static final long RELMATRIXREPORT = -100;
	public static final long TOLALLOCATIONREPORT = -101;
	public static final long SUBJRANKREPORT = -102;
	
	public static final int MONTECARLOREPORT = -1;
	public static final int RISKEXPORTREPORT = -2;
	
	//Specific Report IDs.
	public static final Long NEW_USER_REPORT = -80L;
	public static final Long RISK_DETAIL_REPORT = -10001L;
	
	public static final int INDENTIFACTIONREVIEW = 1;
	public static final int INDENTIFACTIONAPPROVAL = 2;
	public static final int EVALUATIONREVIEW = 3;
	public static final int EVALUATIONAPPROVAL = 4;
	public static final int MITREVIEW = 5;
	public static final int MITAPPROVAL = 6;
	
	public static final long EXPORTRISKSEXCEL = -50000L;
	public static final long EXPORTRISKSXML = -50001L;
	
	public static final long DAYSMSEC = 1000*60*60*24;

	public static final Long  RAW_TEXT_EMAIL = -100L;
	public static final Long  RAW_HTML_EMAIL = -200L;
	
	public static final int MARGINS = 1;
	public static final int NOMARGINS = 0;
	public static final int TREATED = 0;
	public static final int INHERENT = 1;
	public static final int CURRENT = 2;
	
	public static final String NUM_REPORT_THREADS = "2";
	public static final String NUM_MONTE_THREADS = "1";
	public static final String NUM_EMAIL_THREADS = "1";
	
	//Used by QRMCategoryURLGenerator
	
	public static final int OWNERS = 0;
	public static final int MANAGERS = 1;
	public static final int STATUS = 2;
	public static final int RISK = 3;
	public static final int CATEGORIES = 4;
	public static final int TOLERANCE = 5;
	public static final int MULITOLERANCE = 6;
	
	// Monte Carlo Engine
	public static final int ITERATIONSMAX = 10000;
	public static final long DEFAULTITERATIONS = 1000;
	
	public static final int NUMCONTINGENCY_ITERATIONS = 2000;

	
	public static final int PRE = 0;
	public static final int POST = 1;
	public static final int BOTH = 99;
	
	public static final int Captcha_width = 200;
	public static final int Captcha_height = 50;
	
	public static final int ToleranceExtreme = 5;
	public static final int ToleranceHigh = 4;
	public static final int ToleranceSignificant = 3;
	public static final int ToleranceModerate = 2;
	public static final int ToleranceLow = 1;
	
	public static final String ADMINEMAIL_MSG = "EMAILADMIN_MSG";
	public static final String REPORT_MSG = "REPORT_MSG";
	public static final String EMAIL_MSG = "EMAIL_MSG";
	public static final String MONTE_MSG = "MONTE_MSG";
	public static final String MQ_MSG = "MQ_MSG";
	public static final String NOTIFICATION_MSG = "NOTIFICATION_MSG";
	
	public static long fact(final long n) {
		if (n == 0) {
			return 1;
		}
		return n * fact(n - 1);
	}
	public static Double getDoubleJS(final Object obj) {
		Double val = 0.0;
		try {
			if (obj instanceof Long) {
				val = ((Long) obj).doubleValue();
			}
			if (obj instanceof String) {
				val = Double.parseDouble(((String) obj));
			}
			if (obj instanceof Double) {
				val = (Double) obj;
			}
		} catch (Exception e) {
			val = null;
		}
		return val;
	}
}

import java.io.*;
import java.math.BigDecimal;
import java.util.Random;

/**
 * Created by stephan on 8/23/16.
 */
public class ExampleCreator {
    private long identifier;

    private long salesQuotationId = 0;
    private long salesQuotationSentTo = 0;
    private long salesQuotationContains = 0;
    private long salesOrderId = 0;
    private long salesOrderContains = 0;
    private long salesOrderReceivedFrom = 0;
    private long purchOrderId = 0;
    private long delivID = 0;
    private long purchInvoiceID = 0;
    private long salesInvoiceID = 0;
    private long ticketID = 0;
    private long salesInvoiceID2 = 0;
    private long purchInvoiceID2 = 0;
    private long ticketID2 = 0;
    private long salesInvoiceID3 = 0;
    private long purchInvoiceID3 = 0;

    private long ticketSelection;
    private long empCount;
    private long cusCount;
    private long logCount;
    private long venCount;
    private long prdCount;

    private String vertices;
    private String edges;

    private String verticesNew;
    private String edgesNew;

    private int number;

    private float bad = 0.7f;
    private float good = 0.1f;

    private Random rand;

    private BigDecimal revenue;
    private BigDecimal expense;

    private BigDecimal revenueCit;
    private BigDecimal expenseCit;

    private BigDecimal revenueCit2;
    private BigDecimal expenseCit2;

    private  BigDecimal totalMinus;

    /**
     * @param firstId
     * @param empCount
     * @param cusCount
     * @param logCount
     * @param venCount
     * @param prdCount
     */
    public ExampleCreator(long firstId, long empCount, long cusCount, long logCount, long venCount, long prdCount) {
        identifier = firstId;

        this.empCount = empCount;
        this.cusCount = cusCount;
        this.logCount = logCount;
        this.venCount = venCount;
        this.prdCount = prdCount;

        rand = new Random();
    }

    /**
     *
     * @param number
     * @param ticketSelection 0 = 0, 1=only bad, 2=only late, 3=both
     */
    public void start(int number, long ticketSelection, BigDecimal revenue, BigDecimal expense) {
        this.number = number;
        this.ticketSelection = ticketSelection;

        this.revenue = revenue;
        this.expense = expense;

        BigDecimal half = BigDecimal.valueOf(rand.nextDouble());
        BigDecimal quart = BigDecimal.valueOf(rand.nextDouble());

        if (ticketSelection == 3) {
            totalMinus = revenue.multiply(BigDecimal.ONE.add(
                    BigDecimal.valueOf((15 + (20 - 15) * rand.nextDouble()) / 100))).setScale(2,BigDecimal.ROUND_HALF_UP);;


            revenueCit = totalMinus.multiply(half).multiply(quart).multiply(BigDecimal.valueOf(-1)).setScale(2,BigDecimal.ROUND_HALF_UP);
            expenseCit = totalMinus.multiply(half).multiply(BigDecimal.ONE.subtract(quart)).setScale(2,BigDecimal.ROUND_HALF_UP);

            quart = BigDecimal.valueOf(rand.nextDouble());
            revenueCit2 = totalMinus.multiply(BigDecimal.ONE.subtract(half)).multiply(quart).setScale(2,BigDecimal.ROUND_HALF_UP);
            expenseCit2 = totalMinus.multiply(BigDecimal.ONE.subtract(half)).multiply(BigDecimal.ONE.subtract(quart)).setScale(2,BigDecimal.ROUND_HALF_UP);

        } else if (ticketSelection == 1 || ticketSelection == 2) {
            totalMinus = revenue.multiply(
                    BigDecimal.ONE.add(
                            BigDecimal.valueOf((8 + (12 - 8) * rand.nextDouble()) /100)));

            if (ticketSelection == 2) {
                revenueCit = totalMinus.multiply(half).multiply(BigDecimal.valueOf(-1)).setScale(2,BigDecimal.ROUND_HALF_UP);
                expenseCit = totalMinus.multiply(BigDecimal.ONE.subtract(half)).setScale(2,BigDecimal.ROUND_HALF_UP);
            } else {
                revenueCit2 = totalMinus.multiply(half).multiply(BigDecimal.valueOf(-1)).setScale(2,BigDecimal.ROUND_HALF_UP);
                expenseCit2 = totalMinus.multiply(BigDecimal.ONE.subtract(half)).setScale(2,BigDecimal.ROUND_HALF_UP);
            }

        }


        vertices = readFile(System.getProperty("user.home") + "/verticesC.json");
        edges = readFile(System.getProperty("user.home") + "/edgesC.json");

        verticesNew = vertices;
        edgesNew = edges;



        salesQuotationId = identifier;
        verticesNew = verticesNew.replaceAll("SalesQuotationID", String.valueOf(salesQuotationId));

        edgesNew = edgesNew.replaceAll("SalesQuotationID", String.valueOf(salesQuotationId));
        replaceNextIdentifier();//sentTo
        salesQuotationSentTo = getNextCus();
        edgesNew = edgesNew.replaceFirst("NextCus", String.valueOf(salesQuotationSentTo));
        replaceNextIdentifier();//sentBy
        edgesNew = edgesNew.replaceFirst("NextEmp", String.valueOf(getNextEmp()));
        replaceNextIdentifier();//SQLine
        salesQuotationContains = getNextPrd();
        edgesNew = edgesNew.replaceFirst("NextProduct", String.valueOf(salesQuotationContains));

        identifier++;//salesOrder
        salesOrderId = identifier;
        verticesNew = verticesNew.replaceAll("SalesOrderID", String.valueOf(salesOrderId));

        edgesNew = edgesNew.replaceAll("SalesOrderID", String.valueOf(salesOrderId));
        replaceNextIdentifier();//receivedFrom
        salesOrderReceivedFrom = salesQuotationSentTo;
        edgesNew = edgesNew.replaceFirst("SalesQuotation.SentTo", String.valueOf(salesOrderReceivedFrom));
        replaceNextIdentifier();//processedBy
        edgesNew = edgesNew.replaceFirst("NextEmp", String.valueOf(getNextEmp()));
        replaceNextIdentifier();//basedOn
        replaceNextIdentifier();//SOLine
        salesOrderContains = salesQuotationContains;
        edgesNew = edgesNew.replaceFirst("SalesQuotation.Contains",String.valueOf(salesOrderContains));

        identifier++;//purchOrder
        purchOrderId = identifier;
        verticesNew = verticesNew.replaceAll("PurchOrderID", String.valueOf(purchOrderId));

        edgesNew = edgesNew.replaceAll("PurchOrderID", String.valueOf(purchOrderId));
        replaceNextIdentifier();//serves
        replaceNextIdentifier();//processedBy
        edgesNew = edgesNew.replaceFirst("NextEmp", String.valueOf(getNextEmp()));
        replaceNextIdentifier();//placedAt
        edgesNew = edgesNew.replaceFirst("NextVen", String.valueOf(getNextVen()));
        replaceNextIdentifier();//POLine
        edgesNew = edgesNew.replaceFirst("SalesOrder.Contains", String.valueOf(salesOrderContains));

        identifier++;//deliveriyNote
        delivID = identifier;
        verticesNew = verticesNew.replaceAll("DelivID", String.valueOf(delivID));

        edgesNew = edgesNew.replaceAll("DelivID", String.valueOf(delivID));
        replaceNextIdentifier();//contains
        replaceNextIdentifier();//operatedBy
        edgesNew = edgesNew.replaceFirst("NextLog", String.valueOf(getNextLog()));

        identifier++;//purchinvoice
        purchInvoiceID = identifier;
        verticesNew = verticesNew.replaceAll("PurchInvoiceID", String.valueOf(purchInvoiceID));
        verticesNew = verticesNew.replaceAll("ExpenseERP", String.valueOf(purchInvoiceID));

        edgesNew = edgesNew.replaceAll("PurchInvoiceID", String.valueOf(purchInvoiceID));
        replaceNextIdentifier();//createdFor

        identifier++;//salesInvoice
        salesInvoiceID = identifier;
        verticesNew = verticesNew.replaceAll("SalesInvoiceID", String.valueOf(salesInvoiceID));
        verticesNew = verticesNew.replaceAll("RevenueERP", String.valueOf(revenue));

        edgesNew = edgesNew.replaceAll("SalesInvoiceID", String.valueOf(salesInvoiceID));
        replaceNextIdentifier();

        if (ticketSelection == 2 || ticketSelection == 3) {
            identifier++;//ticket
            ticketID = identifier;
            verticesNew = verticesNew.replaceAll("TicketID", String.valueOf(ticketID));

            edgesNew = edgesNew.replaceAll("TicketID", String.valueOf(ticketID));
            replaceNextIdentifier();//openedBy
            edgesNew = edgesNew.replaceFirst("SalesOrder.ReceivedFrom", String.valueOf(getClient(salesOrderReceivedFrom)));
            replaceNextIdentifier();//createdBy
            edgesNew = edgesNew.replaceFirst("NextUser", String.valueOf(getNextUser()));
            replaceNextIdentifier();//allocatedTo
            edgesNew = edgesNew.replaceFirst("NextUser", String.valueOf(getNextUser()));

            identifier++;
            salesInvoiceID2 = identifier;
            verticesNew = verticesNew.replaceAll("SalesInvoice2ID", String.valueOf(salesInvoiceID2));
            verticesNew = verticesNew.replaceAll("RevenueCIT", String.valueOf(revenueCit));

            edgesNew = edgesNew.replaceAll("SalesInvoice2ID", String.valueOf(salesInvoiceID2));
            replaceNextIdentifier();//createdFor

            identifier++;
            purchInvoiceID2 = identifier;
            verticesNew = verticesNew.replaceAll("PurchInvoice2ID", String.valueOf(purchInvoiceID2));
            verticesNew = verticesNew.replaceAll("ExpenseCIT", String.valueOf(expenseCit));

            edgesNew = edgesNew.replaceAll("PurchInvoice2ID", String.valueOf(purchInvoiceID2));
            replaceNextIdentifier();//createdFor

        }

        if (ticketSelection == 1 || ticketSelection == 3) {
            if (ticketSelection == 1) {
                verticesNew = verticesNew.replaceAll("TicketID", "NotSet");
                edgesNew = edgesNew.replaceAll("TicketID", "NotSet");
                edgesNew = edgesNew.replaceFirst("SalesOrder.ReceivedFrom", "NotSet");
                edgesNew = edgesNew.replaceFirst("NextUser", "NotSet");
                edgesNew = edgesNew.replaceFirst("NextUser", "NotSet");
                verticesNew = verticesNew.replaceAll("SalesInvoice2ID", "NotSet");
                verticesNew = verticesNew.replaceAll("RevenueCIT", "NotSet");
                edgesNew = edgesNew.replaceAll("SalesInvoice2ID", "NotSet");
                verticesNew = verticesNew.replaceAll("PurchInvoice2ID", "NotSet");
                verticesNew = verticesNew.replaceAll("ExpenseCIT", "NotSet");
                edgesNew = edgesNew.replaceAll("PurchInvoice2ID", "NotSet");

                edgesNew  = edgesNew.replaceFirst("Identifier", "NotSet");//openedBy
                edgesNew  = edgesNew.replaceFirst("Identifier", "NotSet");//createdBy
                edgesNew  = edgesNew.replaceFirst("Identifier", "NotSet");//allocatedTo
                edgesNew  = edgesNew.replaceFirst("Identifier", "NotSet");//createdFor
                edgesNew  = edgesNew.replaceFirst("Identifier", "NotSet");//createdFor
            }
            identifier++;//ticket
            ticketID2 = identifier;
            verticesNew = verticesNew.replaceAll("Ticket2ID", String.valueOf(ticketID2));

            edgesNew = edgesNew.replaceAll("Ticket2ID", String.valueOf(ticketID2));
            replaceNextIdentifier();//openedBy
            edgesNew = edgesNew.replaceFirst("SalesOrder.ReceivedFrom", String.valueOf(getClient(salesOrderReceivedFrom)));
            replaceNextIdentifier();//createdBy
            edgesNew = edgesNew.replaceFirst("NextUser", String.valueOf(getNextUser()));
            replaceNextIdentifier();//allocatedTo
            edgesNew = edgesNew.replaceFirst("NextUser", String.valueOf(getNextUser()));

            identifier++;
            salesInvoiceID3 = identifier;
            verticesNew = verticesNew.replaceAll("SalesInvoice3ID", String.valueOf(salesInvoiceID3));
            verticesNew = verticesNew.replaceAll("Revenue2CIT", String.valueOf(revenueCit2));

            edgesNew = edgesNew.replaceAll("SalesInvoice3ID", String.valueOf(salesInvoiceID3));
            replaceNextIdentifier();//createdFor

            identifier++;
            purchInvoiceID3 = identifier;
            verticesNew = verticesNew.replaceAll("PurchInvoice3ID", String.valueOf(purchInvoiceID3));
            verticesNew = verticesNew.replaceAll("Expense2CIT", String.valueOf(expenseCit2));

            edgesNew = edgesNew.replaceAll("PurchInvoice3ID", String.valueOf(purchInvoiceID3));
            replaceNextIdentifier();//createdFor
        }

        StringBuilder sbV = new StringBuilder();
        String[] vertexLine = verticesNew.split(System.getProperty("line.separator"));
        for (int i = 0; i < vertexLine.length; i++) {
            if (!vertexLine[i].contains("ID") && !vertexLine[i].contains("NotSet")) {
                sbV.append(vertexLine[i] + System.getProperty("line.separator"));
            }
        }
        StringBuilder sbE = new StringBuilder();
        String[] edgeLine = edgesNew.split(System.getProperty("line.separator"));
        for (int i = 0; i < edgeLine.length; i++) {
            if (!edgeLine[i].contains("Identifier") && !edgeLine[i].contains("NotSet")) {
                sbE.append(edgeLine[i] + System.getProperty("line.separator"));
            }
        }


        writeFile(System.getProperty("user.home") + "/verticesC" + number + ".json", sbV.toString());
        writeFile(System.getProperty("user.home") + "/edgesC" + number + ".json", sbE.toString());
        identifier++;
    }

    private void replaceNextIdentifier(){
        identifier++;
        edgesNew = edgesNew.replaceFirst("Identifier", String.valueOf(identifier));
    }

    private long getNextEmp() {
        long id;
        if (ticketSelection == 1 || ticketSelection == 2 || ticketSelection == 3) {
            if (rand.nextFloat() >= good) {     // 90%
                if (rand.nextFloat() <= bad) {  // 70%
                    id = 4; // bad
                } else {
                    id = rand.nextInt((3 - 2) + 1) + 2; //normal
                }
            } else {
                id = 1;
            }
        } else {
            id = rand.nextInt((3 - 1) + 1) + 1; //normal or good
        }
        return id;
    }

    private long getNextPrd() {
        long id;
        if (ticketSelection == 1 || ticketSelection == 3) {
            id = rand.nextInt((9 - 8) + 1) + 8;     // bad
        } else {
            return rand.nextInt((7 - 5) + 1) + 5;   //normal good
        }
        return  id;
    }

    private long getNextCus() {
        long id;
        if (ticketSelection == 1 || ticketSelection == 3) {
            if (rand.nextFloat() >= good) {     // 90 %
                if (rand.nextFloat() <= bad) {  // 70%
                    id = 4 + empCount + prdCount;   //bad
                } else {
                    id = rand.nextInt((3 - 2) + 1) + 2 + empCount + prdCount;   //normal
                }
            } else {
                id = 1 + empCount + prdCount;   //good
            }
        } else {
            id = rand.nextInt((3 - 1) + 1) + 1 + empCount + prdCount;
        }
        return id;
    }

    private long getNextLog() {
        long id;
        if (ticketSelection == 1) {
            if (rand.nextFloat() <= bad) {  //70%
                if (rand.nextFloat() <= 0.5f) {     // 50%
                    id = 3 + empCount + prdCount + cusCount; //bad
                } else {
                    id = 2 + empCount + prdCount + cusCount; //normal
                }
            } else {
                id = 2 + empCount + prdCount + cusCount;  // normal
            }
        } else if (ticketSelection == 2 || ticketSelection == 3) {
            if (rand.nextFloat() >= good) { // 90%
                id = 3 + empCount + prdCount + cusCount; //bad
            } else {
                id = 2 + empCount + prdCount + cusCount; //normal
            }
        } else {
            if (rand.nextFloat() >= 0.5f) { // 50%
                id = 1 + empCount + prdCount + cusCount; //good
            } else {
                id = 2 + empCount + prdCount + cusCount; //normal
            }
        }
        return id;
    }

    private long getNextVen() {         //17-20
        long id;
        if (ticketSelection == 1 || ticketSelection == 2 || ticketSelection == 3) {
            if (rand.nextFloat() <= bad) {  // 70%
                id = 4 + empCount + prdCount + cusCount + logCount; //bad
            } else {
                id = rand.nextInt((3 - 2) + 1) + 2 + empCount + prdCount + cusCount + logCount; // normal
            }
        } else {
            id = rand.nextInt((3 - 1) + 1) + 1 + empCount + prdCount + cusCount + logCount; // normal or good
        }
        return id;
    }

    private long getNextUser() {
        return getNextEmp()  + empCount + prdCount + cusCount + logCount + venCount;
    }

    private long getClient(Long customerId) {
        return customerId + cusCount + logCount + venCount + empCount;
    }


    private String readFile(String path) {
        BufferedReader br = null;
        StringBuilder sb = null;
        try {
            br = new BufferedReader(new FileReader(path));
            sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

    private void writeFile(String path, String content) {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new FileWriter(path));
            writer.write(content);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                writer.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void appendAllFiles(int fileCount) {
        StringBuilder sb = new StringBuilder();

        for (int i = 0; i < fileCount; i++) {
            sb.append(readFile(System.getProperty("user.home") + "/verticesC" + i + ".json"));
        }
        sb.append(readFile(System.getProperty("user.home") + "/masterData.json"));
        writeFile(System.getProperty("user.home") + "/nodes.json", sb.toString());

        sb = new StringBuilder();
        for (int i = 0; i < fileCount; i++) {
            sb.append(readFile(System.getProperty("user.home") + "/edgesC" + i + ".json"));
        }
        writeFile(System.getProperty("user.home") + "/edges.json", sb.toString());
    }

}

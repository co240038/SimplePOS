// Aquino: Implemented the abstract Product class, main SimplePOS GUI frame, theme colors, fonts, button styling, and product images integration.
// Masillam: Integrated all panels into the final GUI and implemented the Checkout dialog with purchase summary and confirmation button
// File: SimplePOS.java
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

// -----------------------------
// ABSTRACT CLASS: Product     
// -----------------------------
abstract class Product {
    protected String name;
    protected double price;

    public Product(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() { return name; }
    public double getPrice() { return price; }

    @Override
    public String toString() {
        return name + " - ₱" + String.format("%.2f", price);
    }
}

// -----------------------------
// SUBCLASSES: FoodItem / DrinkItem
// -----------------------------
class FoodItem extends Product {
    public FoodItem(String name, double price) { super(name, price); }
}

class DrinkItem extends Product {
    public DrinkItem(String name, double price) { super(name, price); }
}

// -----------------------------
// CLASS: CartItem
// -----------------------------

// Jalilula: Implemented the CartItem class, handling product-quantity logic, and created the Cart Table (JTable) with the table model for displaying cart data.
class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product) {
        this.product = product;
        this.quantity = 1;
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }

    public void increaseQuantity() { quantity++; }
    public double getSubtotal() { return product.getPrice() * quantity; }
}

// -----------------------------
// CLASS: Cart
// -----------------------------
class Cart {
    private List<CartItem> items = new ArrayList<>();

    public void addItem(Product p) {
        for (CartItem item : items) {
            if (item.getProduct().getName().equals(p.getName())) {
                item.increaseQuantity();
                return;
            }
        }
        items.add(new CartItem(p));
    }

    public void clear() { items.clear(); }
    public List<CartItem> getItems() { return items; }

    public double getSubtotal() {
        double sum = 0;
        for (CartItem item : items) sum += item.getSubtotal();
        return sum;
    }

    public double getTax() { return getSubtotal() * 0.12; } // 12% tax
    public double getTotal() { return getSubtotal() + getTax(); }
}

// -----------------------------
// MAIN : SimplePOS Frame
// -----------------------------
public class SimplePOS extends JFrame {
    private Cart cart = new Cart();
    private DefaultTableModel tableModel;
    private JLabel lblSubtotal, lblTax, lblTotal;

    // App theme colors
    private final Color ACCENT_RED = new Color(207, 15, 31);
    private final Color ACCENT_YELLOW = new Color(255, 195, 0);
    private final Color SIDEBAR_BG = new Color(220, 40, 40);
    private final Color CONTENT_BG = new Color(250, 250, 250);

    public SimplePOS() {
        setTitle("Simple POS Fast Food");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setSize(900, 600);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(12, 12));

        // fonts
        Font headerFont = new Font("Segoe UI", Font.BOLD, 18);
        Font normalFont = new Font("Segoe UI", Font.PLAIN, 14);
        Font bigBold = new Font("Segoe UI", Font.BOLD, 20);

        // -----------------------------
        // LEFT PANEL: Product Buttons
        // -----------------------------

        // Blanco: Developed FoodItem and DrinkItem subclasses and created the Product Panel with product buttons and item-click handling.
        JPanel productPanel = new JPanel();
        productPanel.setLayout(new BorderLayout(8, 8));
        productPanel.setBackground(SIDEBAR_BG);
        productPanel.setBorder(BorderFactory.createEmptyBorder(12, 12, 12, 12));

        JLabel lblProducts = new JLabel("Menu");
        lblProducts.setForeground(Color.WHITE);
        lblProducts.setFont(bigBold);
        productPanel.add(lblProducts, BorderLayout.NORTH);

        JPanel grid = new JPanel(new GridLayout(0, 2, 10, 10));
        grid.setOpaque(false);

        // product list
        Product[] products = {
                new FoodItem("Burger", 80),
                new FoodItem("Fries", 50),
                new FoodItem("Pizza", 120),
                new DrinkItem("Coke", 30),
                new DrinkItem("IcedTea", 40),
                new DrinkItem("Water", 20)
        };

        for (Product p : products) {
            JButton btn = createProductButton(p, normalFont);
            grid.add(btn);
        }

        JScrollPane prodScroll = new JScrollPane(grid);
        prodScroll.setBorder(null);
        prodScroll.setOpaque(false);
        prodScroll.getViewport().setOpaque(false);
        prodScroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        productPanel.add(prodScroll, BorderLayout.CENTER);

        // -----------------------------
        // CENTER PANEL: Cart Table
        // -----------------------------
        String[] columns = {"Item", "Qty", "Price", "Subtotal"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override public boolean isCellEditable(int row, int column) { return false; }
        };

        JTable table = new JTable(tableModel);
        table.setFont(normalFont);
        table.setRowHeight(36);
        table.getTableHeader().setFont(headerFont);
        table.getTableHeader().setBackground(ACCENT_YELLOW);

        // widen Subtotal column so text doesn't get cut
        table.getColumnModel().getColumn(3).setPreferredWidth(120);

        // Right align numbers
        DefaultTableCellRenderer right = new DefaultTableCellRenderer();
        right.setHorizontalAlignment(SwingConstants.RIGHT);
        table.getColumnModel().getColumn(2).setCellRenderer(right);
        table.getColumnModel().getColumn(3).setCellRenderer(right);

        JScrollPane scroll = new JScrollPane(table);
        scroll.setBorder(BorderFactory.createTitledBorder("Cart"));

        JPanel centerPanel = new JPanel(new BorderLayout(8,8));
        centerPanel.setBackground(CONTENT_BG);
        centerPanel.setBorder(new EmptyBorder(12,12,12,12));
        centerPanel.add(scroll, BorderLayout.CENTER);

        // -----------------------------
        // RIGHT PANEL: Totals + Buttons
        // -----------------------------
        JPanel rightPanel = new JPanel(new BorderLayout(12,12));
        rightPanel.setBackground(CONTENT_BG);
        rightPanel.setBorder(new EmptyBorder(12,12,12,12));

        JPanel totalsPanel = new JPanel(new GridLayout(6,1,6,6));
        totalsPanel.setBackground(CONTENT_BG);
        totalsPanel.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(220,220,220)),
                new EmptyBorder(12,12,12,12)
        ));

        JLabel lblSubTitle = new JLabel("Totals");
        lblSubTitle.setFont(headerFont);
        totalsPanel.add(lblSubTitle);

        JPanel row1 = new JPanel(new BorderLayout());
        row1.setBackground(CONTENT_BG);
        row1.add(new JLabel("Subtotal:"), BorderLayout.WEST);
        lblSubtotal = new JLabel("₱0.00");
        row1.add(lblSubtotal, BorderLayout.EAST);
        totalsPanel.add(row1);

        JPanel row2 = new JPanel(new BorderLayout());
        row2.setBackground(CONTENT_BG);
        row2.add(new JLabel("Tax (12%):"), BorderLayout.WEST);
        lblTax = new JLabel("₱0.00");
        row2.add(lblTax, BorderLayout.EAST);
        totalsPanel.add(row2);

        JPanel row3 = new JPanel(new BorderLayout());
        row3.setBackground(CONTENT_BG);
        row3.add(new JLabel("Total:"), BorderLayout.WEST);
        lblTotal = new JLabel("₱0.00");
        lblTotal.setFont(new Font("Segoe UI", Font.BOLD, 16));
        row3.add(lblTotal, BorderLayout.EAST);
        totalsPanel.add(row3);

        // Buttons
        JPanel control = new JPanel(new GridLayout(1,2,8,8));
        control.setBackground(CONTENT_BG);

        JButton btnCheckout = new JButton("Checkout");
        stylePrimaryButton(btnCheckout);
        btnCheckout.addActionListener(e -> showCheckoutDialog());

        JButton btnClear = new JButton("Clear Cart");
        styleSecondaryButton(btnClear);
        btnClear.addActionListener(e -> {
            cart.clear();
            updateTable();
        });

        control.add(btnCheckout);
        control.add(btnClear);

        rightPanel.add(totalsPanel, BorderLayout.CENTER);
        rightPanel.add(control, BorderLayout.SOUTH);

        // -----------------------------
        // ADD PANELS
        // -----------------------------
        add(productPanel, BorderLayout.WEST);
        add(centerPanel, BorderLayout.CENTER);
        add(rightPanel, BorderLayout.EAST);

        setVisible(true);
    }

    // -----------------------------
    // Product Button + Image Loading
    // -----------------------------
    private JButton createProductButton(Product p, Font font) {
        JButton btn = new JButton("<html><center>" + p.getName() + "<br>₱" + (int)p.getPrice()
                + "</center></html>");

        btn.setFont(font);
        btn.setVerticalTextPosition(SwingConstants.BOTTOM);
        btn.setHorizontalTextPosition(SwingConstants.CENTER);
        btn.setPreferredSize(new Dimension(150, 110));
        btn.setFocusPainted(false);

        // LOAD IMAGE FROM /src/images/
        String fileName = "/images/" + p.getName() + ".png";
        ImageIcon icon = null;

        try {
            icon = new ImageIcon(getClass().getResource(fileName));
            Image scaled = icon.getImage().getScaledInstance(70, 70, Image.SCALE_SMOOTH);
            icon = new ImageIcon(scaled);
        } catch (Exception ex) {
            System.out.println("Missing image: " + fileName);
        }

        btn.setIcon(icon);

        // Styling
        btn.setBackground(ACCENT_YELLOW);
        btn.setBorder(BorderFactory.createLineBorder(new Color(190, 40, 40)));
        btn.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));

        btn.addActionListener(e -> {
            cart.addItem(p);
            updateTable();
        });

        return btn;
    }

    // -----------------------------
    // Styling helpers
    // -----------------------------
    private void stylePrimaryButton(JButton b) {
        b.setBackground(new Color(220, 40, 40));
        b.setForeground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.BOLD, 16));
    }

    private void styleSecondaryButton(JButton b) {
        b.setBackground(Color.WHITE);
        b.setFont(new Font("Segoe UI", Font.PLAIN, 14));
    }

    // -----------------------------
    // Update cart table and totals
    // -----------------------------
    private void updateTable() {
        tableModel.setRowCount(0);
        for (CartItem item : cart.getItems()) {
            tableModel.addRow(new Object[]{
                    item.getProduct().getName(),
                    item.getQuantity(),
                    "₱" + String.format("%.2f", item.getProduct().getPrice()),
                    "₱" + String.format("%.2f", item.getSubtotal())
            });
        }

        lblSubtotal.setText("₱" + String.format("%.2f", cart.getSubtotal()));
        lblTax.setText("₱" + String.format("%.2f", cart.getTax()));
        lblTotal.setText("₱" + String.format("%.2f", cart.getTotal()));
    }

    // -----------------------------
    // THEMED Checkout Dialog
    // -----------------------------
    private void showCheckoutDialog() {
        if (cart.getItems().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Cart is empty!");
            return;
        }

        JDialog dialog = new JDialog(this, "Checkout", true);
        dialog.setSize(350, 420);
        dialog.setLayout(new BorderLayout());
        dialog.setLocationRelativeTo(this);

        // Top red bar
        JPanel top = new JPanel();
        top.setBackground(ACCENT_RED);
        top.setPreferredSize(new Dimension(100, 60));
        JLabel title = new JLabel("Purchase Summary");
        title.setForeground(Color.WHITE);
        title.setFont(new Font("Segoe UI", Font.BOLD, 20));
        top.add(title);

        // Content
        JPanel body = new JPanel();
        body.setLayout(new BoxLayout(body, BoxLayout.Y_AXIS));
        body.setBorder(new EmptyBorder(20, 20, 20, 20));

        // add items
        for (CartItem item : cart.getItems()) {
            JLabel lbl = new JLabel(
                    item.getProduct().getName() + " x" + item.getQuantity() +
                            " — ₱" + String.format("%.2f", item.getSubtotal())
            );
            lbl.setFont(new Font("Segoe UI", Font.PLAIN, 16));
            lbl.setAlignmentX(Component.CENTER_ALIGNMENT); // keep my comments
            body.add(lbl);
        }

        body.add(Box.createVerticalStrut(20));

        JLabel lblSub = new JLabel("Subtotal: ₱" + String.format("%.2f", cart.getSubtotal()));
        lblSub.setAlignmentX(Component.CENTER_ALIGNMENT); // keep my comments
        JLabel lblTaxA = new JLabel("Tax (12%): ₱" + String.format("%.2f", cart.getTax()));
        lblTaxA.setAlignmentX(Component.CENTER_ALIGNMENT); // keep my comments
        JLabel lblTot = new JLabel("Total: ₱" + String.format("%.2f", cart.getTotal()));
        lblTot.setFont(new Font("Segoe UI", Font.BOLD, 18));
        lblTot.setAlignmentX(Component.CENTER_ALIGNMENT); // keep my comments

        body.add(lblSub);
        body.add(lblTaxA);
        body.add(lblTot);

        // OK button
        JButton ok = new JButton("OK");
        stylePrimaryButton(ok);
        ok.addActionListener(e -> dialog.dispose());
        JPanel bottom = new JPanel(new FlowLayout(FlowLayout.CENTER)); // keep my comments
        bottom.add(ok);

        dialog.add(top, BorderLayout.NORTH);
        dialog.add(body, BorderLayout.CENTER);
        dialog.add(bottom, BorderLayout.SOUTH);

        dialog.setVisible(true);
    }

    // -----------------------------
    // ENTRY POINT
    // -----------------------------
    public static void main(String[] args) {
        SwingUtilities.invokeLater(SimplePOS::new);
    }
}

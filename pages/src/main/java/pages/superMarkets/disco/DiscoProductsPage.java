package pages.superMarkets.disco;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;

import pages.model.Product;

public class DiscoProductsPage extends DiscoHomePage{

	@FindBy(className = "filaListaDetalle")
	private List<WebElement> productElementList;
	@FindBy(partialLinkText = ">>")
	private WebElement nextPageButton;

	public DiscoProductsPage(WebDriver driver) {
		super(driver);
		this.waitUntilElementIsDisplayedInPage(By.className("tM"), TimeUnit.SECONDS.toMillis(10));
	}

	private Product setProduct(WebElement productElement) {
		Product product = new Product();
		product.setDescription(productElement.findElement(By.className("link-lista2")).getText());
		List<WebElement> tdList = productElement.findElements(By.tagName("td"));
		Double price = null;
		for (WebElement td : tdList) {
			if (td.getText().trim().matches("\\$\\d+(\\.\\d+)")) {
				price = Double.valueOf(td.getText().replace(",", ".").replace("$", "").trim());
				break;
			}
		}
		product.setPrice(price);
		System.out.println(product.toString());
		return product;
	}

	private List<Product> setAllProductsFromPage() {
		List<Product> productList = new ArrayList<Product>();
		for (WebElement productElement : this.productElementList) {
			productList.add(this.setProduct(productElement));
		}
		return productList;
	}

	private List<Product> setAllProductsFromSubCategory() {
		List<Product> productList = new ArrayList<Product>();
		while(this.webElementIsDisplayedInPage(By.partialLinkText(">>"))) {
			productList.addAll(this.setAllProductsFromPage());
			this.nextPageButton.click();
			boolean pageHasLoaded = this.waitUntilPageStopsLoading();
		}
		productList.addAll(this.setAllProductsFromPage());
		return productList;
	}

	private List<Product> setAllProductsFromCategory(WebElement categoryElement) {
		List<Product> productList = new ArrayList<Product>();
		for (int i = 0; i < categoryElement.findElements(By.tagName("table")).size(); i++) {
			if (categoryElement.findElements(By.tagName("table")).get(i).isDisplayed()) {
				WebElement subCategoryElement = categoryElement.findElements(By.tagName("table")).get(i);
				System.out.println(subCategoryElement.findElement(By.tagName("a")).getText());
				subCategoryElement.findElement(By.tagName("a")).click();
				boolean pageHasLoaded = this.waitUntilPageStopsLoading();
				if (this.webElementIsDisplayedInElement(subCategoryElement, By.className("visible"))) {
					productList.addAll(this.setAllProductsFromCategory(subCategoryElement));
				} else {
					productList.addAll(this.setAllProductsFromSubCategory());
				}
				subCategoryElement.findElement(By.tagName("a")).click();
			}
		}
		return productList;
	}

	private Integer getSubCategoriesQuantity(List<WebElement> subCategoriesElementList) {
		Integer output = 0;
		for (WebElement subCategoryElement : subCategoriesElementList) {
			if (subCategoryElement.isDisplayed()) {
				output++;
			}
		}
		return output;
	}

	public List<Product> setAllProducts() {
		List<Product> productList = new ArrayList<Product>();
		for (WebElement categoryElement : this.categoriesElementList) {
			System.out.println(categoryElement.findElement(By.tagName("a")).getText());
			categoryElement.findElement(By.tagName("a")).click();
			productList.addAll(this.setAllProductsFromCategory(categoryElement));
			System.out.println(new Date());
			System.out.println(productList.size());
		}
		return productList;
	}

}

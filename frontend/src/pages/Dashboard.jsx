import { useState, useEffect, useCallback, useMemo } from 'react';
import { useLocation, Link } from 'react-router-dom';
import { getDashboard } from '../api/dashboard';
import { getProducts } from '../api/products';
import LoadingSpinner from '../components/LoadingSpinner';
import {
  HiOutlineCube,
  HiOutlineArchive,
  HiOutlineExclamationCircle,
  HiOutlineCurrencyDollar,
  HiOutlineArrowNarrowRight,
  HiOutlineCheckCircle,
  HiOutlineTrendingUp,
  HiOutlineRefresh,
  HiOutlinePencilAlt,
  HiOutlineShieldCheck,
} from 'react-icons/hi';

export default function Dashboard() {
  const [dashboardData, setDashboardData] = useState(null);
  const [productsList, setProductsList] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const location = useLocation();

  const fetchData = useCallback(async () => {
    try {
      setError('');
      setLoading(true);
      const [dashRes, prodRes] = await Promise.all([
        getDashboard(),
        getProducts().catch(() => ({ data: [] })),
      ]);
      setDashboardData(dashRes.data);
      setProductsList(prodRes.data || []);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load dashboard metrics');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchData();
  }, [fetchData, location.key]);

  // Calculate real inventory value from actual product costs/prices
  const inventoryValue = useMemo(() => {
    if (!productsList || productsList.length === 0) return 0;
    return productsList.reduce((acc, p) => {
      const price = Number(p.costPrice ?? p.sellingPrice ?? 0);
      return acc + (p.quantity || 0) * (isNaN(price) ? 0 : price);
    }, 0);
  }, [productsList]);

  // Currency formatter
  const formattedInventoryValue = useMemo(() => {
    return new Intl.NumberFormat('en-US', {
      style: 'currency',
      currency: 'USD',
      maximumFractionDigits: 2,
    }).format(inventoryValue);
  }, [inventoryValue]);

  // Top products by stock volume for the distribution chart
  const topProducts = useMemo(() => {
    if (!productsList || productsList.length === 0) return [];
    return [...productsList]
      .sort((a, b) => (b.quantity || 0) - (a.quantity || 0))
      .slice(0, 5);
  }, [productsList]);

  const maxProductQty = useMemo(() => {
    if (topProducts.length === 0) return 1;
    return Math.max(...topProducts.map((p) => p.quantity || 0), 1);
  }, [topProducts]);

  // Stock health metrics calculated from actual data
  const totalProductsCount = dashboardData?.totalProducts ?? 0;
  const lowStockCount = dashboardData?.lowStockCount ?? 0;
  const healthyCount = Math.max(0, totalProductsCount - lowStockCount);
  const healthPercent = totalProductsCount > 0
    ? Math.round((healthyCount / totalProductsCount) * 100)
    : 100;

  if (loading) return <LoadingSpinner />;

  if (error) {
    return (
      <div className="p-4 sm:p-6 bg-red-50 border border-red-200 text-red-700 rounded-2xl flex items-center justify-between gap-4">
        <div>
          <p className="font-semibold text-sm sm:text-base">Unable to load dashboard</p>
          <p className="text-xs sm:text-sm mt-0.5">{error}</p>
        </div>
        <button
          onClick={fetchData}
          className="px-4 py-2 bg-red-100 hover:bg-red-200 text-red-800 text-sm font-medium rounded-xl transition-colors flex items-center gap-1.5"
        >
          <HiOutlineRefresh className="w-4 h-4" />
          <span>Retry</span>
        </button>
      </div>
    );
  }

  const kpiCards = [
    {
      title: 'Total Products',
      value: totalProductsCount,
      subtext: totalProductsCount === 1 ? '1 unique SKU cataloged' : `${totalProductsCount} unique SKUs cataloged`,
      icon: HiOutlineCube,
      iconBg: 'bg-blue-100 text-blue-600',
      borderAccent: 'border-l-blue-600',
    },
    {
      title: 'Total Stock Units',
      value: (dashboardData?.totalStockQuantity ?? 0).toLocaleString(),
      subtext: 'Units available across inventory',
      icon: HiOutlineArchive,
      iconBg: 'bg-emerald-100 text-emerald-600',
      borderAccent: 'border-l-emerald-600',
    },
    {
      title: 'Low Stock Items',
      value: lowStockCount,
      subtext: lowStockCount > 0 ? 'Requires immediate restock' : 'All items are well stocked',
      icon: HiOutlineExclamationCircle,
      iconBg: lowStockCount > 0 ? 'bg-amber-100 text-amber-600' : 'bg-emerald-100 text-emerald-600',
      badge: lowStockCount > 0 ? 'Action Needed' : 'Healthy',
      badgeColor: lowStockCount > 0 ? 'bg-amber-50 text-amber-700 border-amber-200' : 'bg-emerald-50 text-emerald-700 border-emerald-200',
      borderAccent: lowStockCount > 0 ? 'border-l-amber-500' : 'border-l-emerald-500',
    },
    {
      title: 'Inventory Value',
      value: formattedInventoryValue,
      subtext: 'Based on actual unit cost/price',
      icon: HiOutlineCurrencyDollar,
      iconBg: 'bg-indigo-100 text-indigo-600',
      borderAccent: 'border-l-indigo-600',
    },
  ];

  return (
    <div className="space-y-6 sm:space-y-8 animate-in fade-in duration-200">
      {/* 2. KPI Summary Cards Grid - Equal height, proper spacing, no text truncation */}
      <section aria-label="Dashboard Overview KPIs">
        <div className="grid grid-cols-1 sm:grid-cols-2 lg:grid-cols-4 gap-4 sm:gap-5">
          {kpiCards.map((card) => (
            <div
              key={card.title}
              className={`bg-white rounded-2xl p-5 sm:p-6 border border-gray-200/80 shadow-xs hover:shadow-md transition-all duration-200 border-l-4 ${card.borderAccent} flex flex-col justify-between h-full min-h-[148px]`}
            >
              <div>
                {/* Card Title & Icon */}
                <div className="flex items-center justify-between gap-3 mb-2">
                  <p className="text-xs font-semibold text-gray-500 uppercase tracking-wider">
                    {card.title}
                  </p>
                  <div className={`w-10 h-10 rounded-xl flex items-center justify-center flex-shrink-0 ${card.iconBg} shadow-xs`}>
                    <card.icon className="w-5 h-5 stroke-2" />
                  </div>
                </div>

                {/* Big Number & Status Badge */}
                <div className="flex items-center gap-2.5 flex-wrap">
                  <p className="text-2xl sm:text-3xl font-extrabold text-gray-900 tracking-tight">
                    {card.value}
                  </p>
                  {card.badge && (
                    <span className={`px-2.5 py-0.5 rounded-full text-[11px] font-bold border ${card.badgeColor}`}>
                      {card.badge}
                    </span>
                  )}
                </div>
              </div>

              {/* Description without truncation */}
              <div className="mt-3 pt-3 border-t border-gray-100 text-xs text-gray-500 leading-normal">
                {card.subtext}
              </div>
            </div>
          ))}
        </div>
      </section>

      {/* 3 & 4. Inventory Overview & Analytics Section */}
      <section className="grid grid-cols-1 lg:grid-cols-3 gap-6">
        {/* 4. Inventory Health Card */}
        <div className="bg-white rounded-2xl p-6 border border-gray-200/80 shadow-xs flex flex-col justify-between h-full">
          <div>
            <div className="flex items-center justify-between mb-4 gap-2">
              <h2 className="text-base font-bold text-gray-900 flex items-center gap-2">
                <HiOutlineShieldCheck className="w-5 h-5 text-blue-600 flex-shrink-0" />
                <span>Inventory Health</span>
              </h2>
              <span className={`px-2.5 py-1 rounded-full text-xs font-bold border shrink-0 ${
                healthPercent >= 90
                  ? 'bg-emerald-50 text-emerald-700 border-emerald-200'
                  : healthPercent >= 70
                  ? 'bg-amber-50 text-amber-700 border-amber-200'
                  : 'bg-red-50 text-red-700 border-red-200'
              }`}>
                {healthPercent}% Optimal
              </span>
            </div>

            {/* Health Progress Bar */}
            <div className="space-y-2 mt-4">
              <div className="h-3 w-full bg-gray-100 rounded-full overflow-hidden flex">
                <div
                  className="bg-emerald-500 h-full transition-all duration-500"
                  style={{ width: `${healthPercent}%` }}
                  title={`Healthy: ${healthyCount} ${healthyCount === 1 ? 'product' : 'products'}`}
                />
                <div
                  className="bg-amber-500 h-full transition-all duration-500"
                  style={{ width: `${100 - healthPercent}%` }}
                  title={`Low Stock: ${lowStockCount} ${lowStockCount === 1 ? 'product' : 'products'}`}
                />
              </div>

              <div className="flex items-center justify-between text-xs text-gray-600 pt-1">
                <span className="flex items-center gap-1.5">
                  <span className="w-2.5 h-2.5 rounded-full bg-emerald-500 flex-shrink-0" />
                  <span>Healthy ({healthyCount} {healthyCount === 1 ? 'item' : 'items'})</span>
                </span>
                <span className="flex items-center gap-1.5">
                  <span className="w-2.5 h-2.5 rounded-full bg-amber-500 flex-shrink-0" />
                  <span>Low Stock ({lowStockCount} {lowStockCount === 1 ? 'item' : 'items'})</span>
                </span>
              </div>
            </div>

            {/* Metric Highlights */}
            <div className="grid grid-cols-2 gap-3 mt-6 pt-6 border-t border-gray-100">
              <div className="p-3 bg-gray-50/80 rounded-xl">
                <span className="text-[11px] font-semibold text-gray-500 uppercase tracking-wider">In-Stock Rate</span>
                <p className="text-xl font-bold text-gray-900 mt-0.5">{healthPercent}%</p>
              </div>
              <div className="p-3 bg-gray-50/80 rounded-xl">
                <span className="text-[11px] font-semibold text-gray-500 uppercase tracking-wider">Low Stock Ratio</span>
                <p className={`text-xl font-bold mt-0.5 ${lowStockCount > 0 ? 'text-amber-600' : 'text-gray-900'}`}>
                  {totalProductsCount > 0 ? `${Math.round((lowStockCount / totalProductsCount) * 100)}%` : '0%'}
                </p>
              </div>
            </div>
          </div>

          <div className="mt-6 pt-4 border-t border-gray-100">
            <Link
              to="/products"
              className="text-xs font-semibold text-blue-600 hover:text-blue-700 flex items-center justify-between group"
            >
              <span>View full product catalog</span>
              <HiOutlineArrowNarrowRight className="w-4 h-4 transition-transform group-hover:translate-x-1" />
            </Link>
          </div>
        </div>

        {/* 3. Stock Distribution Chart */}
        <div className="bg-white rounded-2xl p-6 border border-gray-200/80 shadow-xs lg:col-span-2 flex flex-col justify-between h-full">
          <div>
            <div className="flex items-center justify-between mb-4 gap-2">
              <div>
                <h2 className="text-base font-bold text-gray-900 flex items-center gap-2">
                  <HiOutlineTrendingUp className="w-5 h-5 text-indigo-600 flex-shrink-0" />
                  <span>Stock Distribution</span>
                </h2>
                <p className="text-xs text-gray-500 mt-0.5">Top products by inventory volume</p>
              </div>
              <span className="text-xs font-semibold text-gray-500 uppercase tracking-wider shrink-0 bg-gray-100 px-2.5 py-1 rounded-lg">
                {productsList.length} {productsList.length === 1 ? 'SKU' : 'Total SKUs'}
              </span>
            </div>

            {/* Responsive Distribution Bars with Dynamic Grammar */}
            {topProducts.length > 0 ? (
              <div className="space-y-4 mt-5">
                {topProducts.map((p) => {
                  const qty = p.quantity || 0;
                  const unitLabel = qty === 1 ? 'unit' : 'units';
                  const percentage = Math.round((qty / maxProductQty) * 100);
                  const isLow = p.lowStock || false;

                  return (
                    <div key={p.id} className="group">
                      <div className="flex items-center justify-between text-xs sm:text-sm font-medium mb-1.5 gap-2">
                        <div className="flex items-center gap-2 min-w-0 pr-2">
                          <span className="text-gray-900 font-semibold truncate" title={p.name}>
                            {p.name}
                          </span>
                          <span className="px-1.5 py-0.5 rounded-md bg-gray-100 text-gray-600 font-mono text-[11px] font-medium shrink-0">
                            {p.sku}
                          </span>
                        </div>
                        <div className="flex items-center gap-2 shrink-0">
                          <span className="font-bold text-gray-900 tabular-nums">
                            {qty.toLocaleString()} {unitLabel}
                          </span>
                          {isLow && (
                            <span className="px-1.5 py-0.5 rounded-md text-[10px] font-bold bg-amber-100 text-amber-800">
                              Low
                            </span>
                          )}
                        </div>
                      </div>

                      {/* Bar Track using actual quantity percentage */}
                      <div className="h-2.5 w-full bg-gray-100 rounded-full overflow-hidden">
                        <div
                          className={`h-full rounded-full transition-all duration-500 ${
                            isLow
                              ? 'bg-gradient-to-r from-amber-400 to-amber-500'
                              : 'bg-gradient-to-r from-blue-500 to-indigo-600'
                          }`}
                          style={{ width: `${Math.max(percentage, qty > 0 ? 5 : 0)}%` }}
                        />
                      </div>
                    </div>
                  );
                })}
              </div>
            ) : (
              <div className="py-12 text-center text-gray-400">
                <HiOutlineCube className="w-10 h-10 mx-auto text-gray-300 mb-2" />
                <p className="text-sm font-medium text-gray-600">No product inventory yet</p>
                <p className="text-xs text-gray-400 mt-1">Add products to visualize your inventory distribution</p>
              </div>
            )}
          </div>

          <div className="mt-6 pt-4 border-t border-gray-100 flex items-center justify-between text-xs text-gray-500">
            <span>Showing top inventory contributors</span>
            <Link
              to="/products"
              className="font-semibold text-blue-600 hover:text-blue-700 flex items-center gap-1 group"
            >
              <span>Explore all</span>
              <HiOutlineArrowNarrowRight className="w-3.5 h-3.5 transition-transform group-hover:translate-x-1" />
            </Link>
          </div>
        </div>
      </section>

      {/* 5. Low Stock Products Section */}
      <section className="bg-white rounded-2xl border border-gray-200/80 shadow-xs overflow-hidden">
        <div className="p-5 sm:p-6 border-b border-gray-100 flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
          <div className="flex items-center gap-3">
            <div className="w-10 h-10 rounded-xl bg-amber-100 text-amber-700 flex items-center justify-center flex-shrink-0 shadow-xs">
              <HiOutlineExclamationCircle className="w-5 h-5 stroke-2" />
            </div>
            <div>
              <div className="flex items-center gap-2.5 flex-wrap">
                <h2 className="text-lg font-bold text-gray-900 tracking-tight">Low Stock Products</h2>
                <span className={`px-2.5 py-0.5 rounded-full text-xs font-bold border ${
                  lowStockCount > 0
                    ? 'bg-amber-50 text-amber-800 border-amber-200'
                    : 'bg-emerald-50 text-emerald-800 border-emerald-200'
                }`}>
                  {lowStockCount} {lowStockCount === 1 ? 'item' : 'items'}
                </span>
              </div>
              <p className="text-xs text-gray-500 mt-0.5">
                Products with available quantity at or below their effective threshold
              </p>
            </div>
          </div>

          <Link
            to="/products"
            className="inline-flex items-center gap-1.5 px-3.5 py-2 text-xs font-semibold text-gray-700 hover:text-blue-600 bg-gray-50 hover:bg-blue-50 border border-gray-200 hover:border-blue-200 rounded-xl transition-all self-start sm:self-auto shrink-0"
          >
            <span>Manage Inventory</span>
            <HiOutlineArrowNarrowRight className="w-3.5 h-3.5" />
          </Link>
        </div>

        {/* Table or Empty State */}
        {dashboardData?.lowStockProducts && dashboardData.lowStockProducts.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="bg-gray-50/75 border-b border-gray-200 text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                  <th scope="col" className="px-5 py-3.5 sm:px-6">Product</th>
                  <th scope="col" className="px-4 py-3.5">SKU</th>
                  <th scope="col" className="px-4 py-3.5">Stock Level</th>
                  <th scope="col" className="px-4 py-3.5">Available Qty</th>
                  <th scope="col" className="px-4 py-3.5">Threshold</th>
                  <th scope="col" className="px-4 py-3.5">Status</th>
                  <th scope="col" className="px-5 py-3.5 sm:px-6 text-right">Action</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {dashboardData.lowStockProducts.map((product) => {
                  const effectiveThresh = product.lowStockThreshold ?? 10;
                  const qty = product.quantity || 0;
                  const ratio = Math.min(100, Math.round((qty / (effectiveThresh || 1)) * 100));

                  return (
                    <tr
                      key={product.id}
                      className="hover:bg-amber-50/40 transition-colors group"
                    >
                      {/* Product Name */}
                      <td className="px-5 py-4 sm:px-6">
                        <div className="font-semibold text-gray-900 text-sm">{product.name}</div>
                        {product.description && (
                          <div className="text-xs text-gray-400 truncate max-w-xs mt-0.5">
                            {product.description}
                          </div>
                        )}
                      </td>

                      {/* SKU */}
                      <td className="px-4 py-4">
                        <span className="inline-flex px-2 py-0.5 rounded-md bg-gray-100 text-gray-700 font-mono text-xs font-medium">
                          {product.sku}
                        </span>
                      </td>

                      {/* Visual Ratio Bar */}
                      <td className="px-4 py-4 w-36">
                        <div className="flex items-center gap-2">
                          <div className="flex-1 h-2 bg-gray-100 rounded-full overflow-hidden">
                            <div
                              className={`h-full rounded-full ${
                                qty === 0
                                  ? 'bg-red-500'
                                  : 'bg-amber-500'
                              }`}
                              style={{ width: `${Math.max(ratio, qty > 0 ? 8 : 0)}%` }}
                            />
                          </div>
                          <span className="text-[11px] font-bold text-gray-500 w-8 text-right tabular-nums">
                            {ratio}%
                          </span>
                        </div>
                      </td>

                      {/* Available Qty */}
                      <td className="px-4 py-4">
                        <span className={`inline-flex items-center px-2.5 py-1 rounded-lg text-xs font-bold ${
                          qty === 0
                            ? 'bg-red-100 text-red-800'
                            : 'bg-amber-100 text-amber-800'
                        }`}>
                          {qty} {qty === 1 ? 'unit' : 'units'}
                        </span>
                      </td>

                      {/* Threshold */}
                      <td className="px-4 py-4 text-xs font-semibold text-gray-700">
                        {effectiveThresh} {effectiveThresh === 1 ? 'unit' : 'units'}
                      </td>

                      {/* Status */}
                      <td className="px-4 py-4">
                        <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-red-50 text-red-700 border border-red-200">
                          <span className="w-1.5 h-1.5 rounded-full bg-red-500 animate-pulse" />
                          <span>Low Stock</span>
                        </span>
                      </td>

                      {/* Action */}
                      <td className="px-5 py-4 sm:px-6 text-right">
                        <Link
                          to={`/products/${product.id}/edit`}
                          className="inline-flex items-center gap-1 px-3 py-1.5 text-xs font-semibold text-blue-600 hover:text-blue-800 bg-blue-50 hover:bg-blue-100 rounded-lg transition-colors"
                          title="Restock or update product"
                        >
                          <HiOutlinePencilAlt className="w-3.5 h-3.5" />
                          <span>Update</span>
                        </Link>
                      </td>
                    </tr>
                  );
                })}
              </tbody>
            </table>
          </div>
        ) : (
          /* Attractive Empty State */
          <div className="py-14 px-4 text-center">
            <div className="w-16 h-16 rounded-2xl bg-emerald-50 text-emerald-600 flex items-center justify-center mx-auto mb-4 border border-emerald-100 shadow-xs">
              <HiOutlineCheckCircle className="w-8 h-8" />
            </div>
            <h3 className="text-base sm:text-lg font-bold text-gray-900 tracking-tight">
              All inventory levels are healthy!
            </h3>
            <p className="text-xs sm:text-sm text-gray-500 max-w-md mx-auto mt-1.5 leading-relaxed">
              No products are currently at or below their low-stock threshold. Your inventory buffer is in optimal standing.
            </p>
            <div className="mt-5">
              <Link
                to="/products/new"
                className="inline-flex items-center gap-1.5 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs sm:text-sm font-semibold rounded-xl shadow-xs transition-colors"
              >
                <span>Add More Products</span>
              </Link>
            </div>
          </div>
        )}
      </section>
    </div>
  );
}

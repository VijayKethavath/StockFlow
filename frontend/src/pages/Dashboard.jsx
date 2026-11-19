import { useState, useEffect } from 'react';
import { getDashboard } from '../api/dashboard';
import LoadingSpinner from '../components/LoadingSpinner';
import { HiOutlineCube, HiOutlineChartBar, HiOutlineExclamation } from 'react-icons/hi';

export default function Dashboard() {
  const [data, setData] = useState(null);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    const fetchDashboard = async () => {
      try {
        const response = await getDashboard();
        setData(response.data);
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load dashboard');
      } finally {
        setLoading(false);
      }
    };
    fetchDashboard();
  }, []);

  if (loading) return <LoadingSpinner />;

  if (error) {
    return (
      <div className="p-4 bg-red-50 border border-red-200 text-red-700 rounded-lg">
        {error}
      </div>
    );
  }

  const cards = [
    {
      title: 'Total Products',
      value: data?.totalProducts ?? 0,
      icon: HiOutlineCube,
      bg: 'bg-blue-50',
      iconBg: 'bg-blue-100',
      iconColor: 'text-blue-600',
      valueColor: 'text-blue-600',
    },
    {
      title: 'Total Stock',
      value: data?.totalStockQuantity ?? 0,
      icon: HiOutlineChartBar,
      bg: 'bg-green-50',
      iconBg: 'bg-green-100',
      iconColor: 'text-green-600',
      valueColor: 'text-green-600',
    },
    {
      title: 'Low Stock',
      value: data?.lowStockCount ?? 0,
      icon: HiOutlineExclamation,
      bg: 'bg-amber-50',
      iconBg: 'bg-amber-100',
      iconColor: 'text-amber-600',
      valueColor: 'text-amber-600',
    },
  ];

  return (
    <div>
      <h1 className="text-2xl font-bold text-gray-900 mb-6">Dashboard</h1>

      {/* Summary Cards */}
      <div className="grid grid-cols-1 md:grid-cols-3 gap-6 mb-8">
        {cards.map((card) => (
          <div key={card.title} className={`${card.bg} rounded-xl p-6 shadow-sm`}>
            <div className="flex items-center justify-between">
              <div>
                <p className="text-sm font-medium text-gray-600">{card.title}</p>
                <p className={`text-3xl font-bold mt-1 ${card.valueColor}`}>{card.value}</p>
              </div>
              <div className={`${card.iconBg} w-12 h-12 rounded-lg flex items-center justify-center`}>
                <card.icon className={`w-6 h-6 ${card.iconColor}`} />
              </div>
            </div>
          </div>
        ))}
      </div>

      {/* Low Stock Products Table */}
      <div className="bg-white rounded-xl shadow-sm border border-gray-200">
        <div className="px-6 py-4 border-b border-gray-200">
          <h2 className="text-lg font-semibold text-gray-900">Low Stock Products</h2>
        </div>

        {data?.lowStockProducts?.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full">
              <thead>
                <tr className="border-b border-gray-200">
                  <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Name
                  </th>
                  <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                    SKU
                  </th>
                  <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Quantity
                  </th>
                  <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Threshold
                  </th>
                  <th className="text-left px-6 py-3 text-xs font-medium text-gray-500 uppercase tracking-wider">
                    Status
                  </th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-200">
                {data.lowStockProducts.map((product) => (
                  <tr key={product.id} className="hover:bg-gray-50">
                    <td className="px-6 py-4 text-sm font-medium text-gray-900">{product.name}</td>
                    <td className="px-6 py-4 text-sm text-gray-500">{product.sku}</td>
                    <td className="px-6 py-4 text-sm text-gray-900">{product.quantity}</td>
                    <td className="px-6 py-4 text-sm text-gray-500">
                      {product.lowStockThreshold}
                    </td>
                    <td className="px-6 py-4">
                      <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium bg-red-100 text-red-700">
                        Low Stock
                      </span>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="px-6 py-12 text-center">
            <HiOutlineChartBar className="w-12 h-12 text-green-400 mx-auto mb-3" />
            <p className="text-gray-500 font-medium">All well stocked!</p>
            <p className="text-gray-400 text-sm mt-1">
              None of your products are below the low stock threshold.
            </p>
          </div>
        )}
      </div>
    </div>
  );
}

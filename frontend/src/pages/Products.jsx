import { useState, useEffect, useCallback } from 'react';
import { Link } from 'react-router-dom';
import { getProducts, deleteProduct } from '../api/products';
import LoadingSpinner from '../components/LoadingSpinner';
import DeleteModal from '../components/DeleteModal';
import {
  HiOutlinePlus,
  HiOutlineSearch,
  HiOutlinePencil,
  HiOutlineTrash,
  HiOutlineCube,
} from 'react-icons/hi';

export default function Products() {
  const [products, setProducts] = useState([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState('');
  const [search, setSearch] = useState('');
  const [deleteTarget, setDeleteTarget] = useState(null);
  const [deleting, setDeleting] = useState(false);

  const fetchProducts = useCallback(async (query) => {
    try {
      setError('');
      const response = await getProducts(query);
      setProducts(response.data);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to load products');
    } finally {
      setLoading(false);
    }
  }, []);

  useEffect(() => {
    fetchProducts();
  }, [fetchProducts]);

  // Debounced search
  useEffect(() => {
    const timer = setTimeout(() => {
      fetchProducts(search);
    }, 300);
    return () => clearTimeout(timer);
  }, [search, fetchProducts]);

  const handleDelete = async () => {
    if (!deleteTarget) return;
    setDeleting(true);
    try {
      await deleteProduct(deleteTarget.id);
      setProducts((prev) => prev.filter((p) => p.id !== deleteTarget.id));
      setDeleteTarget(null);
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to delete product');
    } finally {
      setDeleting(false);
    }
  };

  const formatPrice = (price) => {
    if (price == null) return '—';
    return `$${Number(price).toFixed(2)}`;
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="space-y-6 animate-in fade-in duration-200">
      {/* Header Bar */}
      <div className="flex flex-col sm:flex-row sm:items-center sm:justify-between gap-4">
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-gray-900 tracking-tight">Product Catalog</h2>
          <p className="text-xs sm:text-sm text-gray-500 mt-0.5">
            Manage your inventory items, pricing and stock alerts
          </p>
        </div>
        <Link
          to="/products/new"
          className="inline-flex items-center justify-center gap-2 px-4 py-2.5 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-sm font-semibold rounded-xl shadow-xs transition-colors self-start sm:self-auto"
        >
          <HiOutlinePlus className="w-4 h-4 stroke-2" />
          <span>Add New Product</span>
        </Link>
      </div>

      {/* Search Input */}
      <div className="relative">
        <HiOutlineSearch className="absolute left-3.5 top-1/2 -translate-y-1/2 w-5 h-5 text-gray-400" />
        <input
          type="text"
          value={search}
          onChange={(e) => setSearch(e.target.value)}
          placeholder="Search products by title or SKU..."
          className="w-full pl-11 pr-4 py-3 bg-white border border-gray-200/90 rounded-xl text-sm focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all shadow-2xs"
        />
      </div>

      {error && (
        <div className="p-4 bg-red-50 border border-red-200 text-red-700 text-sm rounded-xl">
          {error}
        </div>
      )}

      {/* Table Card */}
      <div className="bg-white rounded-2xl shadow-xs border border-gray-200/80 overflow-hidden">
        {products.length > 0 ? (
          <div className="overflow-x-auto">
            <table className="w-full text-left border-collapse">
              <thead>
                <tr className="border-b border-gray-200 bg-gray-50/75 text-[11px] font-bold text-gray-500 uppercase tracking-wider">
                  <th scope="col" className="px-5 py-3.5 sm:px-6">Product Details</th>
                  <th scope="col" className="px-4 py-3.5">SKU</th>
                  <th scope="col" className="px-4 py-3.5">In Stock</th>
                  <th scope="col" className="px-4 py-3.5">Cost Price</th>
                  <th scope="col" className="px-4 py-3.5">Selling Price</th>
                  <th scope="col" className="px-4 py-3.5">Status</th>
                  <th scope="col" className="px-5 py-3.5 sm:px-6 text-right">Actions</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-gray-100">
                {products.map((product) => (
                  <tr key={product.id} className="hover:bg-gray-50/80 transition-colors group">
                    <td className="px-5 py-4 sm:px-6">
                      <div className="font-semibold text-gray-900 text-sm">{product.name}</div>
                      {product.description && (
                        <div className="text-xs text-gray-400 truncate max-w-xs mt-0.5">
                          {product.description}
                        </div>
                      )}
                    </td>
                    <td className="px-4 py-4">
                      <span className="inline-flex px-2 py-0.5 rounded-md bg-gray-100 text-gray-700 font-mono text-xs font-medium">
                        {product.sku}
                      </span>
                    </td>
                    <td className="px-4 py-4 font-bold text-gray-900 text-sm">
                      {product.quantity}
                    </td>
                    <td className="px-4 py-4 text-sm text-gray-600 font-medium">
                      {formatPrice(product.costPrice)}
                    </td>
                    <td className="px-4 py-4 text-sm text-gray-900 font-semibold">
                      {formatPrice(product.sellingPrice)}
                    </td>
                    <td className="px-4 py-4">
                      {product.lowStock ? (
                        <span className="inline-flex items-center gap-1.5 px-2.5 py-0.5 rounded-full text-xs font-semibold bg-red-50 text-red-700 border border-red-200">
                          <span className="w-1.5 h-1.5 rounded-full bg-red-500 animate-pulse" />
                          <span>Low Stock</span>
                        </span>
                      ) : (
                        <span className="inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-semibold bg-emerald-50 text-emerald-700 border border-emerald-200">
                          In Stock
                        </span>
                      )}
                    </td>
                    <td className="px-5 py-4 sm:px-6 text-right">
                      <div className="flex items-center justify-end gap-1.5">
                        <Link
                          to={`/products/${product.id}/edit`}
                          className="p-1.5 text-gray-400 hover:text-blue-600 hover:bg-blue-50 rounded-lg transition-colors"
                          title="Edit Product"
                        >
                          <HiOutlinePencil className="w-4 h-4" />
                        </Link>
                        <button
                          type="button"
                          onClick={() => setDeleteTarget(product)}
                          className="p-1.5 text-gray-400 hover:text-red-600 hover:bg-red-50 rounded-lg transition-colors"
                          title="Delete Product"
                        >
                          <HiOutlineTrash className="w-4 h-4" />
                        </button>
                      </div>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        ) : (
          <div className="py-14 px-4 text-center">
            <div className="w-14 h-14 rounded-2xl bg-gray-100 text-gray-400 flex items-center justify-center mx-auto mb-3">
              <HiOutlineCube className="w-7 h-7" />
            </div>
            <p className="text-base font-bold text-gray-800">
              {search ? 'No matching products' : 'No products in catalog'}
            </p>
            <p className="text-xs sm:text-sm text-gray-500 max-w-sm mx-auto mt-1">
              {search
                ? 'Try adjusting your search terms or SKU.'
                : 'Get started by creating your first product item.'}
            </p>
            {!search && (
              <div className="mt-5">
                <Link
                  to="/products/new"
                  className="inline-flex items-center gap-1.5 px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-xs sm:text-sm font-semibold rounded-xl shadow-xs transition-colors"
                >
                  <HiOutlinePlus className="w-4 h-4" />
                  <span>Add First Product</span>
                </Link>
              </div>
            )}
          </div>
        )}
      </div>

      {/* Delete Confirmation Modal */}
      {deleteTarget && (
        <DeleteModal
          product={deleteTarget}
          onCancel={() => setDeleteTarget(null)}
          onConfirm={handleDelete}
        />
      )}
    </div>
  );
}

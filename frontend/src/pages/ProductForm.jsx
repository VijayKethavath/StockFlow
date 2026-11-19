import { useState, useEffect } from 'react';
import { useNavigate, useParams, Link } from 'react-router-dom';
import { getProduct, createProduct, updateProduct } from '../api/products';
import LoadingSpinner from '../components/LoadingSpinner';
import { HiOutlineArrowNarrowLeft } from 'react-icons/hi';

export default function ProductForm() {
  const { id } = useParams();
  const isEdit = Boolean(id);
  const navigate = useNavigate();

  const [form, setForm] = useState({
    name: '',
    sku: '',
    description: '',
    quantity: 0,
    costPrice: '',
    sellingPrice: '',
    lowStockThreshold: '',
  });
  const [loading, setLoading] = useState(isEdit);
  const [saving, setSaving] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    if (!isEdit) return;

    const fetchProduct = async () => {
      try {
        const response = await getProduct(id);
        const p = response.data;
        setForm({
          name: p.name || '',
          sku: p.sku || '',
          description: p.description || '',
          quantity: p.quantity ?? 0,
          costPrice: p.costPrice ?? '',
          sellingPrice: p.sellingPrice ?? '',
          lowStockThreshold: p.lowStockThreshold ?? '',
        });
      } catch (err) {
        setError(err.response?.data?.message || 'Failed to load product');
      } finally {
        setLoading(false);
      }
    };
    fetchProduct();
  }, [id, isEdit]);

  const handleChange = (e) => {
    const { name, value } = e.target;
    setForm((prev) => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setError('');

    if (!form.name.trim()) {
      setError('Product name is required');
      return;
    }
    if (!form.sku.trim()) {
      setError('SKU is required');
      return;
    }

    const quantityValue = form.quantity !== '' && !isNaN(form.quantity)
      ? parseInt(form.quantity, 10)
      : 0;

    const thresholdValue = form.lowStockThreshold !== '' && form.lowStockThreshold != null && !isNaN(form.lowStockThreshold)
      ? parseInt(form.lowStockThreshold, 10)
      : null;

    const payload = {
      name: form.name.trim(),
      sku: form.sku.trim(),
      description: form.description.trim() || null,
      quantity: quantityValue,
      costPrice: form.costPrice !== '' ? parseFloat(form.costPrice) : null,
      sellingPrice: form.sellingPrice !== '' ? parseFloat(form.sellingPrice) : null,
      lowStockThreshold: thresholdValue,
    };

    setSaving(true);
    try {
      if (isEdit) {
        await updateProduct(id, payload);
      } else {
        await createProduct(payload);
      }
      navigate('/products');
    } catch (err) {
      setError(err.response?.data?.message || 'Failed to save product');
    } finally {
      setSaving(false);
    }
  };

  if (loading) return <LoadingSpinner />;

  return (
    <div className="max-w-3xl mx-auto space-y-6 animate-in fade-in duration-200">
      <div className="flex items-center gap-3">
        <Link
          to="/products"
          className="p-2 text-gray-500 hover:text-gray-900 hover:bg-gray-100 rounded-xl transition-colors"
          title="Back to products"
        >
          <HiOutlineArrowNarrowLeft className="w-5 h-5" />
        </Link>
        <div>
          <h2 className="text-xl sm:text-2xl font-bold text-gray-900 tracking-tight">
            {isEdit ? 'Edit Product' : 'Add New Product'}
          </h2>
          <p className="text-xs sm:text-sm text-gray-500 mt-0.5">
            {isEdit ? 'Update details, pricing and threshold' : 'Enter product information and initial inventory count'}
          </p>
        </div>
      </div>

      {error && (
        <div className="p-4 bg-red-50 border border-red-200 text-red-700 text-sm rounded-xl">
          {error}
        </div>
      )}

      <div className="bg-white rounded-2xl shadow-xs border border-gray-200/80 p-6 sm:p-8">
        <form onSubmit={handleSubmit} className="space-y-6">
          <div className="grid grid-cols-1 md:grid-cols-2 gap-5">
            <div>
              <label htmlFor="name" className="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-1.5">
                Product Name <span className="text-red-500">*</span>
              </label>
              <input
                id="name"
                name="name"
                type="text"
                value={form.name}
                onChange={handleChange}
                required
                className="w-full px-4 py-2.5 bg-gray-50/50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                placeholder="e.g., Wireless Mechanical Keyboard"
              />
            </div>

            <div>
              <label htmlFor="sku" className="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-1.5">
                SKU <span className="text-red-500">*</span>
              </label>
              <input
                id="sku"
                name="sku"
                type="text"
                value={form.sku}
                onChange={handleChange}
                required
                className="w-full px-4 py-2.5 bg-gray-50/50 border border-gray-200 rounded-xl text-sm font-mono focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all uppercase"
                placeholder="e.g., KB-MECH-001"
              />
            </div>
          </div>

          <div>
            <label htmlFor="description" className="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-1.5">
              Description
            </label>
            <textarea
              id="description"
              name="description"
              value={form.description}
              onChange={handleChange}
              rows={3}
              className="w-full px-4 py-2.5 bg-gray-50/50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all resize-none"
              placeholder="Optional notes, specifications, or category tags"
            />
          </div>

          <div className="grid grid-cols-1 sm:grid-cols-3 gap-5 pt-2 border-t border-gray-100">
            <div>
              <label htmlFor="quantity" className="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-1.5">
                Available Stock <span className="text-red-500">*</span>
              </label>
              <input
                id="quantity"
                name="quantity"
                type="number"
                min="0"
                value={form.quantity}
                onChange={handleChange}
                required
                className="w-full px-4 py-2.5 bg-gray-50/50 border border-gray-200 rounded-xl text-sm font-bold text-gray-900 focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
              />
            </div>

            <div>
              <label htmlFor="costPrice" className="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-1.5">
                Cost Price ($)
              </label>
              <input
                id="costPrice"
                name="costPrice"
                type="number"
                min="0"
                step="0.01"
                value={form.costPrice}
                onChange={handleChange}
                className="w-full px-4 py-2.5 bg-gray-50/50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                placeholder="0.00"
              />
            </div>

            <div>
              <label htmlFor="sellingPrice" className="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-1.5">
                Selling Price ($)
              </label>
              <input
                id="sellingPrice"
                name="sellingPrice"
                type="number"
                min="0"
                step="0.01"
                value={form.sellingPrice}
                onChange={handleChange}
                className="w-full px-4 py-2.5 bg-gray-50/50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                placeholder="0.00"
              />
            </div>
          </div>

          <div className="pt-2 border-t border-gray-100">
            <div className="max-w-xs">
              <label htmlFor="lowStockThreshold" className="block text-xs font-semibold text-gray-700 uppercase tracking-wider mb-1.5">
                Custom Low-Stock Threshold
              </label>
              <input
                id="lowStockThreshold"
                name="lowStockThreshold"
                type="number"
                min="0"
                value={form.lowStockThreshold}
                onChange={handleChange}
                className="w-full px-4 py-2.5 bg-gray-50/50 border border-gray-200 rounded-xl text-sm focus:bg-white focus:ring-2 focus:ring-blue-500 focus:border-blue-500 outline-none transition-all"
                placeholder="Uses organization default"
              />
              <p className="mt-1.5 text-xs text-gray-400">
                Leave blank to automatically use your organization&apos;s default threshold.
              </p>
            </div>
          </div>

          <div className="flex items-center gap-3 pt-6 border-t border-gray-100">
            <button
              type="submit"
              disabled={saving}
              className="px-6 py-2.5 bg-blue-600 hover:bg-blue-700 text-white font-semibold text-sm rounded-xl shadow-xs hover:shadow transition-all disabled:opacity-50 disabled:cursor-not-allowed"
            >
              {saving ? 'Saving...' : isEdit ? 'Update Product' : 'Create Product'}
            </button>
            <button
              type="button"
              onClick={() => navigate('/products')}
              className="px-5 py-2.5 bg-gray-100 hover:bg-gray-200 text-gray-700 font-semibold text-sm rounded-xl transition-colors"
            >
              Cancel
            </button>
          </div>
        </form>
      </div>
    </div>
  );
}

import { useState, useRef, useEffect } from 'react';
import { useNavigate, useLocation, Link } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';
import {
  HiOutlineMenu,
  HiOutlineLogout,
  HiOutlinePlus,
  HiOutlineOfficeBuilding,
  HiOutlineChevronDown,
} from 'react-icons/hi';

const pageTitles = {
  '/dashboard': { title: 'Dashboard', subtitle: 'Overview & stock metrics' },
  '/products': { title: 'Products', subtitle: 'Manage your product inventory' },
  '/products/new': { title: 'New Product', subtitle: 'Add a new product to inventory' },
  '/settings': { title: 'Settings', subtitle: 'Configure default thresholds & preferences' },
};

export default function Header({ onMobileMenuToggle, onDesktopCollapseToggle, sidebarCollapsed }) {
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [profileOpen, setProfileOpen] = useState(false);
  const dropdownRef = useRef(null);

  // Close dropdown on click outside
  useEffect(() => {
    function handleClickOutside(event) {
      if (dropdownRef.current && !dropdownRef.current.contains(event.target)) {
        setProfileOpen(false);
      }
    }
    document.addEventListener('mousedown', handleClickOutside);
    return () => document.removeEventListener('mousedown', handleClickOutside);
  }, []);

  const currentPage = pageTitles[location.pathname] || {
    title: location.pathname.includes('/edit') ? 'Edit Product' : 'StockFlow',
    subtitle: 'Inventory Management',
  };

  const handleLogout = () => {
    logout();
    navigate('/login');
  };

  const userInitial = user?.email ? user.email.charAt(0).toUpperCase() : 'U';

  return (
    <header className="sticky top-0 z-20 bg-white/95 backdrop-blur-sm border-b border-gray-200/80 px-4 sm:px-6 lg:px-8 py-3.5 transition-all">
      <div className="flex items-center justify-between gap-4">
        {/* Left: Mobile menu button & Page Title */}
        <div className="flex items-center gap-3 sm:gap-4 min-w-0">
          <button
            type="button"
            onClick={onMobileMenuToggle}
            className="p-2 -ml-2 rounded-lg text-gray-600 hover:text-gray-900 hover:bg-gray-100 md:hidden focus:outline-none focus:ring-2 focus:ring-blue-500"
            aria-label="Open sidebar"
          >
            <HiOutlineMenu className="w-6 h-6" />
          </button>

          <div className="min-w-0">
            <h1 className="text-xl sm:text-2xl font-bold text-gray-900 tracking-tight truncate">
              {currentPage.title}
            </h1>
            <p className="hidden sm:block text-xs sm:text-sm text-gray-500 truncate">
              Welcome back, <span className="font-medium text-gray-700">{user?.organizationName || user?.email}</span> &bull; {currentPage.subtitle}
            </p>
          </div>
        </div>

        {/* Right: Quick Action & User Profile */}
        <div className="flex items-center gap-3">
          <Link
            to="/products/new"
            className="hidden sm:inline-flex items-center gap-1.5 px-3.5 py-2 bg-blue-600 hover:bg-blue-700 active:bg-blue-800 text-white text-sm font-medium rounded-lg shadow-sm shadow-blue-500/20 transition-all hover:shadow"
          >
            <HiOutlinePlus className="w-4 h-4 stroke-2" />
            <span>Add Product</span>
          </Link>

          {/* User Profile Dropdown */}
          <div className="relative" ref={dropdownRef}>
            <button
              type="button"
              onClick={() => setProfileOpen(!profileOpen)}
              className="flex items-center gap-2.5 p-1.5 sm:px-3 sm:py-1.5 rounded-lg border border-gray-200 hover:border-gray-300 hover:bg-gray-50/80 transition-colors focus:outline-none focus:ring-2 focus:ring-blue-500"
            >
              <div className="w-8 h-8 rounded-full bg-gradient-to-tr from-blue-600 to-indigo-600 flex items-center justify-center text-white text-sm font-semibold shadow-xs">
                {userInitial}
              </div>
              <div className="hidden md:block text-left">
                <div className="text-xs font-semibold text-gray-900 truncate max-w-[120px]">
                  {user?.organizationName || 'My Org'}
                </div>
                <div className="text-[11px] text-gray-500 truncate max-w-[120px]">
                  {user?.email}
                </div>
              </div>
              <HiOutlineChevronDown className="hidden sm:block w-4 h-4 text-gray-400" />
            </button>

            {/* Dropdown Menu */}
            {profileOpen && (
              <div className="absolute right-0 mt-2 w-64 bg-white rounded-xl shadow-lg border border-gray-100 py-2 z-50 animate-in fade-in slide-in-from-top-1 duration-150">
                <div className="px-4 py-2.5 border-b border-gray-100">
                  <p className="text-xs font-medium text-gray-400 uppercase tracking-wider">Signed in as</p>
                  <p className="text-sm font-semibold text-gray-900 truncate mt-0.5">{user?.email}</p>
                  {user?.organizationName && (
                    <div className="flex items-center gap-1.5 mt-1 text-xs text-blue-600 font-medium">
                      <HiOutlineOfficeBuilding className="w-3.5 h-3.5" />
                      <span className="truncate">{user.organizationName}</span>
                    </div>
                  )}
                </div>

                <div className="py-1">
                  <Link
                    to="/settings"
                    onClick={() => setProfileOpen(false)}
                    className="flex items-center gap-2.5 px-4 py-2 text-sm text-gray-700 hover:bg-gray-50 transition-colors"
                  >
                    <span>Organization Settings</span>
                  </Link>
                </div>

                <div className="border-t border-gray-100 pt-1">
                  <button
                    onClick={handleLogout}
                    className="flex items-center gap-2.5 w-full text-left px-4 py-2 text-sm text-red-600 hover:bg-red-50/80 transition-colors font-medium"
                  >
                    <HiOutlineLogout className="w-4 h-4" />
                    <span>Sign Out</span>
                  </button>
                </div>
              </div>
            )}
          </div>
        </div>
      </div>
    </header>
  );
}

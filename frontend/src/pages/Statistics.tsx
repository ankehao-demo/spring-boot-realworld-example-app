import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { statisticsApi } from '../services/api';
import type { ArticleStatistics } from '../types';

type SortKey = 'title' | 'authorUsername' | 'viewCount' | 'favoritesCount' | 'commentsCount' | 'createdAt';
type SortDirection = 'asc' | 'desc';

export const Statistics = () => {
  const [statistics, setStatistics] = useState<ArticleStatistics[]>([]);
  const [loading, setLoading] = useState(true);
  const [error, setError] = useState<string | null>(null);
  const [sortKey, setSortKey] = useState<SortKey>('viewCount');
  const [sortDirection, setSortDirection] = useState<SortDirection>('desc');

  useEffect(() => {
    const fetchStatistics = async () => {
      try {
        const data = await statisticsApi.getArticleStatistics();
        setStatistics(data);
      } catch {
        setError('Failed to load statistics.');
      } finally {
        setLoading(false);
      }
    };
    fetchStatistics();
  }, []);

  const handleSort = (key: SortKey) => {
    if (sortKey === key) {
      setSortDirection(sortDirection === 'asc' ? 'desc' : 'asc');
    } else {
      setSortKey(key);
      setSortDirection('desc');
    }
  };

  const sortedStatistics = [...statistics].sort((a, b) => {
    const aVal = a[sortKey];
    const bVal = b[sortKey];
    const modifier = sortDirection === 'asc' ? 1 : -1;
    if (typeof aVal === 'string' && typeof bVal === 'string') {
      return aVal.localeCompare(bVal) * modifier;
    }
    if (typeof aVal === 'number' && typeof bVal === 'number') {
      return (aVal - bVal) * modifier;
    }
    return 0;
  });

  const totalViews = statistics.reduce((sum, s) => sum + s.viewCount, 0);
  const totalFavorites = statistics.reduce((sum, s) => sum + s.favoritesCount, 0);
  const totalComments = statistics.reduce((sum, s) => sum + s.commentsCount, 0);

  const SortIcon = ({ columnKey }: { columnKey: SortKey }) => {
    if (sortKey !== columnKey) return <span className="text-blue-200 ml-1">{"\u2195"}</span>;
    return <span className="text-blue-200 ml-1">{sortDirection === 'asc' ? '\u25B2' : '\u25BC'}</span>;
  };

  if (loading) {
    return (
      <div className="text-center py-8">
        <div className="animate-spin rounded-full h-12 w-12 border-b-2 border-blue-500 mx-auto"></div>
      </div>
    );
  }

  if (error) {
    return (
      <div className="max-w-6xl mx-auto px-4 py-8">
        <div className="bg-red-50 border border-red-200 rounded-lg p-4 text-red-700">{error}</div>
      </div>
    );
  }

  return (
    <div className="max-w-6xl mx-auto px-4 py-8">
      <h1 className="text-3xl font-bold text-blue-900 mb-2">Article Statistics</h1>
      <p className="text-blue-600 mb-6">Overview of article performance metrics</p>

      <div className="grid grid-cols-1 md:grid-cols-4 gap-4 mb-8">
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 text-center shadow-sm">
          <p className="text-sm text-blue-700 uppercase tracking-wide">Total Articles</p>
          <p className="text-3xl font-bold text-blue-700 mt-1">{statistics.length}</p>
        </div>
        <div className="bg-blue-50 border border-blue-200 rounded-lg p-6 text-center shadow-sm">
          <p className="text-sm text-blue-700 uppercase tracking-wide">Total Views</p>
          <p className="text-3xl font-bold text-blue-700 mt-1">{totalViews}</p>
        </div>
        <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center shadow-sm">
          <p className="text-sm text-red-700 uppercase tracking-wide">Total Favorites</p>
          <p className="text-3xl font-bold text-red-700 mt-1">{totalFavorites}</p>
        </div>
        <div className="bg-red-50 border border-red-200 rounded-lg p-6 text-center shadow-sm">
          <p className="text-sm text-red-700 uppercase tracking-wide">Total Comments</p>
          <p className="text-3xl font-bold text-red-700 mt-1">{totalComments}</p>
        </div>
      </div>

      <div className="bg-white border border-blue-200 rounded-lg shadow-sm overflow-hidden">
        <table className="min-w-full divide-y divide-blue-200">
          <thead className="bg-blue-600">
            <tr>
              <th
                className="px-6 py-3 text-left text-xs font-medium text-white uppercase tracking-wider cursor-pointer hover:bg-blue-700"
                onClick={() => handleSort('title')}
              >
                Title <SortIcon columnKey="title" />
              </th>
              <th
                className="px-6 py-3 text-left text-xs font-medium text-white uppercase tracking-wider cursor-pointer hover:bg-blue-700"
                onClick={() => handleSort('authorUsername')}
              >
                Author <SortIcon columnKey="authorUsername" />
              </th>
              <th
                className="px-6 py-3 text-right text-xs font-medium text-white uppercase tracking-wider cursor-pointer hover:bg-blue-700"
                onClick={() => handleSort('viewCount')}
              >
                Views <SortIcon columnKey="viewCount" />
              </th>
              <th
                className="px-6 py-3 text-right text-xs font-medium text-white uppercase tracking-wider cursor-pointer hover:bg-blue-700"
                onClick={() => handleSort('favoritesCount')}
              >
                Favorites <SortIcon columnKey="favoritesCount" />
              </th>
              <th
                className="px-6 py-3 text-right text-xs font-medium text-white uppercase tracking-wider cursor-pointer hover:bg-blue-700"
                onClick={() => handleSort('commentsCount')}
              >
                Comments <SortIcon columnKey="commentsCount" />
              </th>
              <th
                className="px-6 py-3 text-right text-xs font-medium text-white uppercase tracking-wider cursor-pointer hover:bg-blue-700"
                onClick={() => handleSort('createdAt')}
              >
                Created <SortIcon columnKey="createdAt" />
              </th>
            </tr>
          </thead>
          <tbody className="bg-white divide-y divide-blue-100">
            {sortedStatistics.map((stat, index) => (
              <tr key={stat.articleId} className={index % 2 === 0 ? 'bg-blue-50 hover:bg-blue-100' : 'bg-red-50 hover:bg-red-100'}>
                <td className="px-6 py-4 whitespace-nowrap">
                  <Link
                    to={`/article/${stat.slug}`}
                    className="text-blue-600 hover:text-blue-800 font-medium"
                  >
                    {stat.title}
                  </Link>
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500">
                  {stat.authorUsername}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 text-right">
                  {stat.viewCount}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 text-right">
                  {stat.favoritesCount}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-900 text-right">
                  {stat.commentsCount}
                </td>
                <td className="px-6 py-4 whitespace-nowrap text-sm text-gray-500 text-right">
                  {new Date(stat.createdAt).toLocaleDateString()}
                </td>
              </tr>
            ))}
            {sortedStatistics.length === 0 && (
              <tr>
                <td colSpan={6} className="px-6 py-8 text-center text-gray-500">
                  No articles found.
                </td>
              </tr>
            )}
          </tbody>
        </table>
      </div>
    </div>
  );
};

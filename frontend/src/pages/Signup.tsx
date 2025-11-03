import { useState } from 'react';
// placement: E - Main Content
export default function Signup() {
  const [submitting, setSubmitting] = useState(false);
  const [error, setError] = useState<string | null>(null);

  async function onSubmit(e: React.FormEvent<HTMLFormElement>) {
    e.preventDefault();
    setError(null);

    const fd = new FormData(e.currentTarget);
    const username = String(fd.get('username') || '').trim();
    const password = String(fd.get('password') || '');
    const confirm = String(fd.get('confirm') || '');

    if (!username) return setError('Username is required.');
    if (password.length < 8) return setError('Password must be at least 8 characters.');
    if (password !== confirm) return setError('Passwords do not match.');

    setSubmitting(true);
    try {
      // TODO: call api.ts -> signup({ username, password })
      // await signup({ username, password });
      (e.currentTarget as HTMLFormElement).reset();
      // optionally navigate or show success message
    } catch (err) {
      setError('Sign up failed. Please try again.');
    } finally {
      setSubmitting(false);
    }
  }

  return (
    <div className="max-w-md mx-auto mt-20 bg-white border rounded p-6 space-y-4">
      <h2 className="text-xl font-semibold">Sign up</h2>

      <form onSubmit={onSubmit} noValidate className="space-y-3">
        <div className="flex flex-col gap-1">
          <label htmlFor="username" className="text-sm font-medium">User Name</label>
          <input
            id="username"
            name="username"
            className="border rounded px-3 py-2 w-full"
            placeholder="Insert user name"
            autoComplete="username"
            required
          />
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="password" className="text-sm font-medium">Password</label>
          <input
            id="password"
            name="password"
            type="password"
            className="border rounded px-3 py-2 w-full"
            placeholder="Insert password"
            autoComplete="new-password"
            minLength={8}
            required
          />
        </div>

        <div className="flex flex-col gap-1">
          <label htmlFor="confirm" className="text-sm font-medium">Confirm Password</label>
          <input
            id="confirm"
            name="confirm"
            type="password"
            className="border rounded px-3 py-2 w-full"
            placeholder="Re-insert password"
            autoComplete="new-password"
            minLength={8}
            required
          />
        </div>

        {error && (
          <div className="text-red-700 text-sm" role="alert" aria-live="assertive">{error}</div>
        )}

        <button
          type="submit"
          className="bg-green-900 text-white rounded px-3 py-2 w-full disabled:opacity-50"
          disabled={submitting}
        >
          {submitting ? 'Signing up…' : 'Sign up'}
        </button>
      </form>
    </div>
  );
}
/*Note: if you find seomthing that is not working or is questionable, please just let me know
    Lg Mike*/
export default function Upload() {
    return (
        <div className="space-y-4">
            <div className="flex items-center gap-3 border-b pb-2">
                <button className="rounded px-3 py-1 border">Documents</button>
                <button className="rounded px-3 py-1 border bg-white">Upload</button>
                <button className="rounded px-3 py-1 border">Disabled</button>
            </div>
            <h2 className="text-lg font-medium">Upload Document</h2>
            <div className="bg-white border rounded p-4 space-y-3 max-w-3xl">
                <div className="flex gap-2">
                    <input className="border rounded px-3 py-2 flex-1" placeholder="Choose file" readOnly />
                    <button className="border rounded px-3">Browse</button>
                    <button className="border rounded px-3">Upload</button>
                </div>
                <div className="grid grid-cols-2 gap-3">
                    <input className="border rounded px-3 py-2" placeholder="Comments" />
                    <select className="border rounded px-3 py-2"><option>Image</option><option>PDF</option><option>Unknown</option></select>
                    <input className="border rounded px-3 py-2 col-span-2" placeholder="Summary" />
                </div>
                <div className="flex items-center gap-6 pt-2">
                    <label className="flex items-center gap-2 text-sm"><input type="checkbox"/> Upload Document</label>
                    <label className="flex items-center gap-2 text-sm"><input type="checkbox"/> grant access</label>
                    <label className="flex items-center gap-2 text-sm"><input type="checkbox"/> Paper free</label>
                </div>
            </div>
        </div>
    );
}
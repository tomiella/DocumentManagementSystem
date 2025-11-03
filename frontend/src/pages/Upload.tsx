export default function Upload() {
    return (
        <div className="space-y-4">
            <div className="flex items-center gap-3  pb-2">
                <button className="bg-emerald-200 rounded px-3 py-1 border">Documents</button>
                <button className="bg-emerald-200 rounded px-3 py-1 border bg-white">Upload</button>
                <button className="bg-emerald-200 rounded px-3 py-1 border">Disabled</button>
            </div>


            <h2 className="text-lg font-medium">Upload Document</h2>
            <div className="bg-white border rounded p-4 space-y-3 max-w-3xl">
                <div className="flex gap-2">
                    <input className="border rounded px-3 py-2 flex-1" placeholder="Choose file" readOnly />
                    <button className="bg-emerald-200 border rounded px-3">Browse</button>
                    <button className="bg-emerald-200 border rounded px-3">Upload</button>
                    <select className="border-b-emerald-600 rounded px-3 py-2 bg-emerald-200" defaultValue="">
                        <option value="" disabled hidden>Select File Type</option>
                        <option>Image</option>
                        <option>PDF</option>
                        <option>Unknown</option>
                    </select>

                </div>
                <div className="grid grid-cols-2 gap-3">
                    <input className="border rounded px-3 py-2" placeholder="Comments" />
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
/*Note: if you find seomthing that is not working or is questionable, please just let me know
    Lg Mike*/
package pet.loyal.provider.view.editpetcard

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import pet.loyal.provider.databinding.LayoutEditPatientCardItemBinding
import pet.loyal.provider.databinding.LayoutPhotoGalleryItemBinding
import pet.loyal.provider.model.PhaseMessage

class PhaseMessageGalleryRecyclerViewAdapter(
    private val imageList: List<String>,
    private val imageItemListener: ImageItemListener
) :
    RecyclerView.Adapter<PhaseMessageGalleryRecyclerViewAdapter.GalleryViewHolder>() {

    interface ImageItemListener {
        fun onClickImage(position: Int)
        fun onClickDelete(position: Int)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GalleryViewHolder {

        val layoutPhotoGalleryItemBinding = LayoutPhotoGalleryItemBinding.inflate(
            LayoutInflater.from(parent.context),
            parent, false
        )

        return GalleryViewHolder(layoutPhotoGalleryItemBinding)
    }

    override fun getItemCount(): Int {
        return imageList.size
    }

    override fun onBindViewHolder(viewHolder: GalleryViewHolder, position: Int) {

        val itemPhaseMessage = imageList[position]
        val viewPhaseMessage = viewHolder.itemBinding


    }

    inner class GalleryViewHolder(val itemBinding: LayoutPhotoGalleryItemBinding):
        RecyclerView.ViewHolder(itemBinding.root)
}